package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import entities.Aluguel;
import util.ConnectionFactory;
import entities.Aluguel.StatusAluguel;

public class daoAluguel {

    // ------------------------------------
    // READ - Buscar todos
    // ------------------------------------
    public List<Aluguel> buscarTodos() {
        List<Aluguel> alugueis = new ArrayList<>();
        String sql = "SELECT id, cliente_id, carro_id, data_solicitacao, data_aprovacao, " +
                     "data_inicio, data_fim_prevista, data_devolucao_real, status, " +
                     "dias_atraso, valor_total, motivo_rejeicao FROM alugueis";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Aluguel aluguel = new Aluguel();
                    aluguel.setId(rs.getLong("id"));
                    aluguel.setClienteId(rs.getLong("cliente_id"));
                    aluguel.setCarroId(rs.getLong("carro_id"));
                    aluguel.setDataSolicitacao(rs.getDate("data_solicitacao") != null ? rs.getDate("data_solicitacao").toString() : null);
                    aluguel.setDataAprovacao(rs.getDate("data_aprovacao") != null ? rs.getDate("data_aprovacao").toString() : null);
                    aluguel.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toString() : null);
                    aluguel.setDataFimPrevista(rs.getDate("data_fim_prevista") != null ? rs.getDate("data_fim_prevista").toString() : null);
                    aluguel.setDataDevolucaoReal(rs.getDate("data_devolucao_real") != null ? rs.getDate("data_devolucao_real").toString() : null);
                    aluguel.setStatus(Aluguel.StatusAluguel.valueOf(rs.getString("status")));
                    aluguel.setDiasAtraso(rs.getInt("dias_atraso"));
                    aluguel.setValorTotal(rs.getBigDecimal("valor_total"));
                    aluguel.setMotivoRejeicao(rs.getString("motivo_rejeicao"));
                    alugueis.add(aluguel);
                }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar alugueis: " + e.getMessage());
            e.printStackTrace();
        }
        return alugueis;
    }

    // ------------------------------------
    // READ BY ID
    // ------------------------------------
    public Aluguel buscarPorId(Long id) {
        Aluguel aluguel = null;
        String sql = "SELECT id, cliente_id, carro_id, data_solicitacao, data_aprovacao, " +
                     "data_inicio, data_fim_prevista, data_devolucao_real, status, " +
                     "dias_atraso, valor_total, motivo_rejeicao FROM alugueis WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    aluguel = new Aluguel();
                    aluguel.setId(rs.getLong("id"));
                    aluguel.setClienteId(rs.getLong("cliente_id"));
                    aluguel.setCarroId(rs.getLong("carro_id"));
                    aluguel.setDataSolicitacao(rs.getDate("data_solicitacao") != null ? rs.getDate("data_solicitacao").toString() : null);
                    aluguel.setDataAprovacao(rs.getDate("data_aprovacao") != null ? rs.getDate("data_aprovacao").toString() : null);
                    aluguel.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toString() : null);
                    aluguel.setDataFimPrevista(rs.getDate("data_fim_prevista") != null ? rs.getDate("data_fim_prevista").toString() : null);
                    aluguel.setDataDevolucaoReal(rs.getDate("data_devolucao_real") != null ? rs.getDate("data_devolucao_real").toString() : null);
                    aluguel.setStatus(Aluguel.StatusAluguel.valueOf(rs.getString("status")));
                    aluguel.setDiasAtraso(rs.getInt("dias_atraso"));
                    aluguel.setValorTotal(rs.getBigDecimal("valor_total"));
                    aluguel.setMotivoRejeicao(rs.getString("motivo_rejeicao"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar aluguel por ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return aluguel;
    }

    // ------------------------------------
    // READ BY CLIENTE_ID
    // ------------------------------------
    public List<Aluguel> buscarPorClienteId(Long clienteId) {
        List<Aluguel> alugueis = new ArrayList<>();
        String sql = "SELECT id, cliente_id, carro_id, data_solicitacao, data_aprovacao, " +
                     "data_inicio, data_fim_prevista, data_devolucao_real, status, " +
                     "dias_atraso, valor_total, motivo_rejeicao FROM alugueis WHERE cliente_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, clienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Aluguel aluguel = new Aluguel();
                    aluguel.setId(rs.getLong("id"));
                    aluguel.setClienteId(rs.getLong("cliente_id"));
                    aluguel.setCarroId(rs.getLong("carro_id"));
                    aluguel.setDataSolicitacao(rs.getDate("data_solicitacao") != null ? rs.getDate("data_solicitacao").toString() : null);
                    aluguel.setDataAprovacao(rs.getDate("data_aprovacao") != null ? rs.getDate("data_aprovacao").toString() : null);
                    aluguel.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toString() : null);
                    aluguel.setDataFimPrevista(rs.getDate("data_fim_prevista") != null ? rs.getDate("data_fim_prevista").toString() : null);
                    aluguel.setDataDevolucaoReal(rs.getDate("data_devolucao_real") != null ? rs.getDate("data_devolucao_real").toString() : null);
                    aluguel.setStatus(Aluguel.StatusAluguel.valueOf(rs.getString("status")));
                    aluguel.setDiasAtraso(rs.getInt("dias_atraso"));
                    aluguel.setValorTotal(rs.getBigDecimal("valor_total"));
                    aluguel.setMotivoRejeicao(rs.getString("motivo_rejeicao"));
                    alugueis.add(aluguel);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar alugueis por Cliente ID: " + clienteId + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return alugueis;
    }

    // ------------------------------------
    // READ BY CLIENTE_ID E STATUS
    // ------------------------------------
    public List<Aluguel> buscarPorClienteIdEStatus(Long clienteId, StatusAluguel status) {
        List<Aluguel> alugueis = new ArrayList<>();
        String sql = "SELECT id, cliente_id, carro_id, data_solicitacao, data_aprovacao, " +
                     "data_inicio, data_fim_prevista, data_devolucao_real, status, " +
                     "dias_atraso, valor_total, motivo_rejeicao FROM alugueis WHERE cliente_id = ? AND status = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, clienteId);
            stmt.setString(2, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
    Aluguel aluguel = new Aluguel();
    aluguel.setId(rs.getLong("id"));
    aluguel.setClienteId(rs.getLong("cliente_id"));
    aluguel.setCarroId(rs.getLong("carro_id"));
    aluguel.setDataSolicitacao(rs.getDate("data_solicitacao") != null ? rs.getDate("data_solicitacao").toString() : null);
    aluguel.setDataAprovacao(rs.getDate("data_aprovacao") != null ? rs.getDate("data_aprovacao").toString() : null);
    aluguel.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toString() : null);
    aluguel.setDataFimPrevista(rs.getDate("data_fim_prevista") != null ? rs.getDate("data_fim_prevista").toString() : null);
    aluguel.setDataDevolucaoReal(rs.getDate("data_devolucao_real") != null ? rs.getDate("data_devolucao_real").toString() : null);
    aluguel.setStatus(Aluguel.StatusAluguel.valueOf(rs.getString("status")));
    aluguel.setDiasAtraso(rs.getInt("dias_atraso"));
    aluguel.setValorTotal(rs.getBigDecimal("valor_total"));
    aluguel.setMotivoRejeicao(rs.getString("motivo_rejeicao"));
    alugueis.add(aluguel);
}
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar alugueis por Cliente ID e Status. Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return alugueis;
    }

    // ------------------------------------
    // Verificar se cliente tem aluguel ativo
    // ------------------------------------
    public boolean clienteTemAluguelAtivo(Long clienteId) {
        String sql = "SELECT COUNT(*) FROM alugueis WHERE cliente_id = ? AND status IN ('PENDENTE', 'APROVADO')";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, clienteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar aluguel ativo do cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------
    // Verificar se carro está alugado
    // ------------------------------------
    public boolean carroEstaAlugado(Long carroId) {
        String sql = "SELECT COUNT(*) FROM alugueis WHERE carro_id = ? AND status IN ('PENDENTE', 'APROVADO')";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, carroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se carro está alugado: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------
    // CREATE - Solicitar aluguel
    // ------------------------------------
    public void inserir(Aluguel aluguel) {
        String sql = "INSERT INTO alugueis (cliente_id, carro_id, data_solicitacao, status, valor_total) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, aluguel.getClienteId());
            stmt.setLong(2, aluguel.getCarroId());
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setString(4, StatusAluguel.PENDENTE.name());
            stmt.setBigDecimal(5, aluguel.getValorTotal());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    aluguel.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir aluguel. Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE - Aprovar aluguel
    // ------------------------------------
    public void aprovar(Long id, String dataInicio) {
        String sql = "UPDATE alugueis SET status = ?, data_aprovacao = ?, data_inicio = ?, data_fim_prevista = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate dataInicioLD = LocalDate.parse(dataInicio);
            LocalDate dataFimPrevista = dataInicioLD.plusDays(5);

            stmt.setString(1, StatusAluguel.APROVADO.name());
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setDate(3, Date.valueOf(dataInicioLD));
            stmt.setDate(4, Date.valueOf(dataFimPrevista));
            stmt.setLong(5, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar aluguel ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE - Rejeitar aluguel
    // ------------------------------------
    public void rejeitar(Long id, String motivoRejeicao) {
        String sql = "UPDATE alugueis SET status = ?, motivo_rejeicao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, StatusAluguel.REJEITADO.name());
            stmt.setString(2, motivoRejeicao);
            stmt.setLong(3, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao rejeitar aluguel ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE - Devolver carro
    // ------------------------------------
    public void devolver(Long id) {
        String sql = "UPDATE alugueis SET status = ?, data_devolucao_real = ?, dias_atraso = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Aluguel aluguel = buscarPorId(id);
            LocalDate hoje = LocalDate.now();
            LocalDate dataFim = LocalDate.parse(aluguel.getDataFimPrevista());
            
            int diasAtraso = 0;
            if (hoje.isAfter(dataFim)) {
                diasAtraso = (int) java.time.temporal.ChronoUnit.DAYS.between(dataFim, hoje);
            }

            stmt.setString(1, StatusAluguel.DEVOLVIDO.name());
            stmt.setDate(2, Date.valueOf(hoje));
            stmt.setInt(3, diasAtraso);
            stmt.setLong(4, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao devolver aluguel ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------
    public void deletar(Long id) {
        String sql = "DELETE FROM alugueis WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao deletar aluguel ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}