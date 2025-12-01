package api;

import static spark.Spark.*;
import com.google.gson.Gson;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import util.GlobalBrDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import entities.Aluguel;
import entities.Cliente;
import entities.Carro;
import entities.Suspensao;
import dao.daoAluguel;
import dao.daoCliente;
import dao.daoCarro;
import dao.daoSuspensao;
import entities.Aluguel.StatusAluguel;
import validation.Rod;

public class apiAluguel {

    private static final daoAluguel dao = new daoAluguel();
    private static final daoCliente daoCliente = new daoCliente();
    private static final daoCarro daoCarro = new daoCarro();
    private static final daoSuspensao daoSuspensao = new daoSuspensao();
    private static final Gson gson = new Gson();

    private static final String APPLICATION_JSON = "application/json";

    public static void Execute() {

        after(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                response.type(APPLICATION_JSON);
            }
        });

        // ------------------------------------
        // GET /alugueis - Buscar todos
        // ------------------------------------
        get("/alugueis", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    return gson.toJson(dao.buscarTodos());
                } catch (Exception e) {
                    response.status(500);
                    return "{\"mensagem\":\"Erro ao buscar alugueis\"}";
                }
            }
        });

        // ------------------------------------
        // GET /aluguel/:id - Buscar por ID
        // ------------------------------------
        get("/aluguel/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                    Aluguel aluguel = dao.buscarPorId(id);

                    if (aluguel == null) {
                        response.status(404);
                        return "{\"mensagem\":\"Aluguel não encontrado\"}";
                    }

                    return gson.toJson(aluguel);
                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\":\"ID inválido\"}";
                } catch (Exception e) {
                    response.status(500);
                    return "{\"mensagem\":\"Erro ao buscar aluguel\"}";
                }
            }
        });

        // ------------------------------------
        // GET /alugueis/cliente/:clienteId - Buscar por cliente
        // ------------------------------------
        get("/alugueis/cliente/:clienteId", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long clienteId = Long.parseLong(request.params(":clienteId").replaceAll("\\D", ""));
                    String status = request.queryParams("status");

                    if (status != null && !status.isEmpty()) {
                        try {
                            StatusAluguel statusEnum = StatusAluguel.valueOf(status.toUpperCase());
                            return gson.toJson(dao.buscarPorClienteIdEStatus(clienteId, statusEnum));
                        } catch (IllegalArgumentException e) {
                            response.status(400);
                            return "{\"mensagem\":\"Status inválido. Use: PENDENTE, APROVADO, REJEITADO, DEVOLVIDO\"}";
                        }
                    }

                    return gson.toJson(dao.buscarPorClienteId(clienteId));
                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\":\"ID do cliente inválido\"}";
                } catch (Exception e) {
                    response.status(500);
                    return "{\"mensagem\":\"Erro ao buscar alugueis do cliente\"}";
                }
            }
        });

        // ------------------------------------
        // POST /aluguel - Solicitar aluguel
        // ------------------------------------
        post("/aluguel", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    java.util.Map<String, Object> body = gson.fromJson(request.body(), java.util.Map.class);

                    // Validações dos campos obrigatórios
                    if (body.get("clienteId") == null) {
                        response.status(400);
                        return "{\"mensagem\":\"Cliente ID é obrigatório\"}";
                    }

                    if (body.get("carroId") == null) {
                        response.status(400);
                        return "{\"mensagem\":\"Carro ID é obrigatório\"}";
                    }

                    if (body.get("diasAluguel") == null) {
                        response.status(400);
                        return "{\"mensagem\":\"Quantidade de dias do aluguel é obrigatória\"}";
                    }

                    Long clienteId = ((Double) body.get("clienteId")).longValue();
                    Long carroId = ((Double) body.get("carroId")).longValue();
                    int diasAluguel = ((Double) body.get("diasAluguel")).intValue();

                    // Validar dias de aluguel
                    Rod.number(diasAluguel, "Dias de aluguel", false, 1.0, 5.0);

                    // Verificar se cliente existe
                    Cliente cliente = daoCliente.buscarPorId(clienteId);
                    if (cliente == null) {
                        response.status(404);
                        return "{\"mensagem\":\"Cliente não encontrado\"}";
                    }

                    // Verificar se carro existe
                    Carro carro = daoCarro.buscarPorId(carroId);
                    if (carro == null) {
                        response.status(404);
                        return "{\"mensagem\":\"Carro não encontrado\"}";
                    }

                    // Verificar se cliente tem suspensão ativa
                    if (daoSuspensao.clienteEstaSuspenso(clienteId)) {
                        response.status(403);
                        return "{\"mensagem\":\"Cliente está suspenso e não pode alugar carros\"}";
                    }

                    // Verificar se cliente já tem aluguel ativo
                    if (dao.clienteTemAluguelAtivo(clienteId)) {
                        response.status(409);
                        return "{\"mensagem\":\"Cliente já possui um aluguel ativo\"}";
                    }

                    // Verificar se carro já está alugado
                    if (dao.carroEstaAlugado(carroId)) {
                        response.status(409);
                        return "{\"mensagem\":\"Carro já está alugado\"}";
                    }

                    // Criar o aluguel
                    Aluguel aluguel = new Aluguel();
                    aluguel.setClienteId(clienteId);
                    aluguel.setCarroId(carroId);

                    LocalDate dataInicio = LocalDate.now();
                    LocalDate dataFimPrevista = dataInicio.plusDays(diasAluguel);

                    // Usar GlobalBrDate para formatar as datas
                    aluguel.setDataInicio(GlobalBrDate.formatLocalDate(dataInicio));
                    aluguel.setDataFimPrevista(GlobalBrDate.formatLocalDate(dataFimPrevista));

                    // Calcular valor total (valor da diária do carro * dias)
                    BigDecimal valorTotal = carro.getValorDiaria().multiply(BigDecimal.valueOf(diasAluguel));
                    aluguel.setValorTotal(valorTotal);

                    // Inserir aluguel
                    dao.inserir(aluguel);

                    response.status(201);
                    return gson.toJson(aluguel);

                } catch (IllegalArgumentException e) {
                    response.status(400);
                    return "{\"mensagem\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
                } catch (Exception e) {
                    response.status(500);
                    e.printStackTrace();
                    return "{\"mensagem\":\"Erro ao solicitar aluguel\"}";
                }
            }
        });

        // ------------------------------------
        // PUT /aluguel/:id/processar - Aprovar ou rejeitar aluguel
        // ------------------------------------
        put("/aluguel/:id/processar", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                    Aluguel aluguel = dao.buscarPorId(id);

                    if (aluguel == null) {
                        response.status(404);
                        return "{\"mensagem\":\"Aluguel não encontrado\"}";
                    }

                    if (aluguel.getStatus() != StatusAluguel.PENDENTE) {
                        response.status(400);
                        return "{\"mensagem\":\"Apenas alugueis pendentes podem ser processados\"}";
                    }

                    // MUDANÇA AQUI: use dd/MM/yyyy HH:mm:ss (formato brasileiro)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime dataSolicitacao = LocalDateTime.parse(aluguel.getDataSolicitacao(), formatter);
                    LocalDateTime agora = LocalDateTime.now();
                    long horasDecorridas = ChronoUnit.HOURS.between(dataSolicitacao, agora);

                    if (horasDecorridas > 24) {
                        response.status(400);
                        return "{\"mensagem\":\"O processamento deve ser feito em até 24 horas após a criação do aluguel. Prazo expirado.\"}";
                    }

                    // Obter dados do corpo da requisição
                    java.util.Map<String, Object> body = gson.fromJson(request.body(), java.util.Map.class);

                    if (body.get("aprovar") == null) {
                        response.status(400);
                        return "{\"mensagem\":\"Campo 'aprovar' é obrigatório (true ou false)\"}";
                    }

                    boolean aprovar = (boolean) body.get("aprovar");

                    if (aprovar) {
                        // Aprovar o aluguel
                        dao.processarAluguel(id, true, null);
                        response.status(200);
                        return "{\"mensagem\":\"Aluguel aprovado com sucesso\"}";
                    } else {
                        // Rejeitar o aluguel
                        if (body.get("motivoRejeicao") == null) {
                            response.status(400);
                            return "{\"mensagem\":\"Motivo da rejeição é obrigatório quando aprovar = false\"}";
                        }

                        String motivoRejeicao = body.get("motivoRejeicao").toString();

                        // Validar motivo
                        Rod.string(motivoRejeicao, "Motivo da rejeição", false, 5, 500);

                        dao.processarAluguel(id, false, motivoRejeicao);
                        response.status(200);
                        return "{\"mensagem\":\"Aluguel rejeitado com sucesso\"}";
                    }

                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\":\"ID inválido\"}";
                } catch (IllegalArgumentException e) {
                    response.status(400);
                    return "{\"mensagem\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
                } catch (Exception e) {
                    response.status(500);
                    e.printStackTrace();
                    return "{\"mensagem\":\"Erro ao processar aluguel\"}";
                }
            }
        });

        // ------------------------------------
        // PUT /aluguel/:id/devolver - Devolver carro
        // ------------------------------------
        put("/aluguel/:id/devolver", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                    Aluguel aluguel = dao.buscarPorId(id);

                    if (aluguel == null) {
                        response.status(404);
                        return "{\"mensagem\":\"Aluguel não encontrado\"}";
                    }

                    if (aluguel.getStatus() != StatusAluguel.APROVADO) {
                        response.status(400);
                        return "{\"mensagem\":\"Apenas alugueis aprovados podem ser devolvidos\"}";
                    }

                    // Calcular dias de atraso
                    LocalDate hoje = LocalDate.now();
                    // MUDANÇA AQUI: use dd/MM/yyyy
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dataFim = LocalDate.parse(aluguel.getDataFimPrevista(), formatter);

                    int diasAtraso = 0;
                    if (hoje.isAfter(dataFim)) {
                        diasAtraso = (int) ChronoUnit.DAYS.between(dataFim, hoje);
                    }

                    // Devolver o carro
                    dao.devolver(id);

                    // Se houver atraso, criar suspensão
                    if (diasAtraso > 0) {
                        Suspensao suspensao = new Suspensao();
                        suspensao.setClienteId(aluguel.getClienteId());
                        suspensao.setAluguelId(id);
                        suspensao.setDiasSuspensao(String.valueOf(diasAtraso));
                        suspensao.setDataInicio(GlobalBrDate.formatLocalDate(hoje));
                        suspensao.setDataFim(GlobalBrDate.formatLocalDate(hoje.plusDays(diasAtraso)));
                        suspensao.setMotivo("Atraso de " + diasAtraso + " dia(s) na devolução do veículo");

                        daoSuspensao.inserir(suspensao);

                        response.status(200);
                        return "{\"mensagem\":\"Carro devolvido. Cliente suspenso por " + diasAtraso
                                + " dia(s) devido ao atraso\"}";
                    }

                    response.status(200);
                    return "{\"mensagem\":\"Carro devolvido com sucesso\"}";

                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\":\"ID inválido\"}";
                } catch (Exception e) {
                    response.status(500);
                    e.printStackTrace();
                    return "{\"mensagem\":\"Erro ao devolver carro\"}";
                }
            }
        });

        // ------------------------------------
        // DELETE /aluguel/:id - Deletar aluguel
        // ------------------------------------
        delete("/aluguel/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                try {
                    Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
                    Aluguel aluguel = dao.buscarPorId(id);

                    if (aluguel == null) {
                        response.status(404);
                        return "{\"mensagem\":\"Aluguel não encontrado\"}";
                    }

                    dao.deletar(id);

                    response.status(200);
                    return "{\"mensagem\":\"Aluguel deletado com sucesso\"}";

                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"mensagem\":\"ID inválido\"}";
                } catch (Exception e) {
                    response.status(500);
                    return "{\"mensagem\":\"Erro ao deletar aluguel\"}";
                }
            }
        });
    }
}