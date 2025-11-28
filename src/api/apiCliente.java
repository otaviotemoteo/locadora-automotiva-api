package api;

import static spark.Spark.*;

import com.google.gson.Gson;

import dao.daoCliente;
import entities.Cliente;
import validation.Rod;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Filter;

public class apiCliente {

  private static final daoCliente dao = new daoCliente();
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
    // GET /clientes - Buscar todos
    // ------------------------------------
    get("/clientes", new Route() {
      @Override
      public Object handle(Request request, Response response) {
        try {
          return gson.toJson(dao.buscarTodos());
        } catch (Exception e) {
          response.status(500);
          return "{\"mensagem\":\"Erro ao buscar clientes\"}";
        }
      }
    });

    // ------------------------------------
    // GET /clientes/:id - Buscar por ID
    // ------------------------------------
    get("/cliente/:id", new Route() {
      @Override
      public Object handle(Request request, Response response) {
        try {
          Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
          Cliente c = dao.buscarPorId(id);

          if (c == null) {
            response.status(404);
            return "{\"mensagem\":\"Cliente não encontrado\"}";
          }

          return gson.toJson(c);
        } catch (NumberFormatException e) {
          response.status(400);
          return "{\"mensagem\":\"ID inválido\"}";
        } catch (Exception e) {
          response.status(500);
          return "{\"mensagem\":\"Erro ao buscar cliente\"}";
        }
      }
    });

    // ------------------------------------
    // POST /cliente - Criar novo cliente
    // ------------------------------------
    post("/cliente", new Route() {
      @Override
      public Object handle(Request request, Response response) {
        try {
          Cliente cliente = gson.fromJson(request.body(), Cliente.class);

          Rod.email(cliente.getEmail(), false);
          Rod.string(cliente.getSenha(), "Senha", false, 3, 255);
          Rod.string(cliente.getNome(), "Nome", false, 3, 255);
          Rod.telefone(cliente.getTelefone(), true);
          Rod.cpf(cliente.getCpf());

          Cliente existenteEmail = dao.buscarPorEmail(cliente.getEmail());
          if (existenteEmail != null) {
            response.status(409);
            return "{\"mensagem\":\"Email já cadastrado\"}";
          }

          Cliente existenteCpf = dao.buscarPorCpf(cliente.getCpf());
          if (existenteCpf != null) {
            response.status(409);
            return "{\"mensagem\":\"CPF já cadastrado\"}";
          }

          Cliente existenteTelefone = dao.buscarPorTelefone(cliente.getTelefone());
          if (existenteTelefone != null) {
            response.status(409);
            return "{\"mensagem\":\"Telefone já cadastrado\"}";
          }

          dao.inserir(cliente);

          cliente.setSenha("********");

          response.status(201);
          return gson.toJson(cliente);

        } catch (IllegalArgumentException e) {
          response.status(400);
          return "{\"mensagem\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        } catch (Exception e) {
          response.status(500);
          return "{\"mensagem\":\"Erro ao cadastrar cliente\"}";
        }
      }
    });

    // ------------------------------------
    // PUT /clientes/:id - Atualizar cliente
    // ------------------------------------
    put("/cliente/:id", new Route() {
      @Override
      public Object handle(Request request, Response response) {
        try {
          Long id = Long.parseLong(request.params(":id"));
          Cliente existente = dao.buscarPorId(id);

          if (existente == null) {
            response.status(404);
            return "{\"mensagem\":\"Cliente não encontrado\"}";
          }

          Cliente atualizado = gson.fromJson(request.body(), Cliente.class);

          Rod.email(atualizado.getEmail(), true);
          Rod.string(atualizado.getNome(), "Nome", true, 3, 255);
          Rod.telefone(atualizado.getTelefone(), true);

          if (atualizado.getEmail() != null) {
            existente.setEmail(atualizado.getEmail());
          }

          if (atualizado.getNome() != null) {
            existente.setNome(atualizado.getNome());
          }

          if (atualizado.getTelefone() != null) {
            existente.setTelefone(atualizado.getTelefone());
          }

          dao.atualizar(existente);

          return gson.toJson(existente);

        } catch (NumberFormatException e) {
          response.status(400);
          return "{\"mensagem\":\"ID inválido\"}";
        } catch (IllegalArgumentException e) {
          response.status(400);
          return "{\"mensagem\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        } catch (Exception e) {
          response.status(500);
          return "{\"mensagem\":\"Erro ao atualizar cliente\"}";
        }
      }
    });

    // ------------------------------------
    // DELETE /clientes/:id - Excluir cliente
    // ------------------------------------
    delete("/cliente/:id", new Route() {
      @Override
      public Object handle(Request request, Response response) {
        try {
          Long id = Long.parseLong(request.params(":id").replaceAll("\\D", ""));
          Cliente c = dao.buscarPorId(id);

          if (c == null) {
            response.status(404);
            return "{\"mensagem\":\"Cliente não encontrado\"}";
          }

          dao.deletar(id);
          return "{\"mensagem\":\"Cliente excluído\"}";
        } catch (NumberFormatException e) {
          response.status(400);
          return "{\"mensagem\":\"ID inválido\"}";
        } catch (Exception e) {
          response.status(500);
          return "{\"mensagem\":\"Erro ao deletar cliente\"}";
        }
      }
    });
  }
}
