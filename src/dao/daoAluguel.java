package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import entities.Aluguel;
import util.ConnectionFactory;
import util.GlobalBrDate;
import entities.Aluguel.StatusAluguel;
import entities.Carro.StatusCarro;

import java.time.format.DateTimeFormatter;

public class daoAluguel {

    // ------------------------------------
    // Método auxiliar para mapear ResultSet
    // ------------------------------------
    private Aluguel mapResultSetToAluguel(ResultSet rs) throws SQLException {
        Aluguel aluguel = new Aluguel();
        aluguel.setId(rs.getLong("id"));
        aluguel.setClienteId(rs.getLong("cliente_id"));
        aluguel.setCarroId(rs.getLong("carro_id"));

        // Timestamp para data_solicitacao
        Timestamp dataSolicitacao = rs.getTimestamp("data_solicitacao");
        aluguel.setDataSolicitacao(dataSolicitacao != null ? GlobalBrDate.formatTimestamp(dataSolicitacao) : null);

        // Timestamp para data_aprovacao
        Timestamp dataAprovacao = rs.getTimestamp("data_aprovacao");
        aluguel.setDataAprovacao(dataAprovacao != null ? GlobalBrDate.formatTimestamp(dataAprovacao) : null);

        // Date para data_inicio
        Date dataInicio = rs.getDate("data_inicio");
        aluguel.setDataInicio(dataInicio != null ? GlobalBrDate.formatLocalDate(dataInicio.toLocalDate()) : null);

        // Date para data_fim_prevista
        Date dataFimPrevista = rs.getDate("data_fim_prevista");
        aluguel.setDataFimPrevista(
                dataFimPrevista != null ? GlobalBrDate.formatLocalDate(dataFimPrevista.toLocalDate()) : null);

        // Timestamp para data_devolucao_real
        Timestamp dataDevolucaoReal = rs.getTimestamp("data_devolucao_real");
        aluguel.setDataDevolucaoReal(
                dataDevolucaoReal != null ? GlobalBrDate.formatTimestamp(dataDevolucaoReal) : null);

        aluguel.setStatus(StatusAluguel.valueOf(rs.getString("status")));
        aluguel.setDiasAtraso(rs.getInt("dias_atraso"));
        aluguel.setValorTotal(rs.getBigDecimal("valor_total"));
        aluguel.setMotivoRejeicao(rs.getString("motivo_rejeicao"));

        return aluguel;
    }

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
                alugueis.add(mapResultSetToAluguel(rs));
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
                    aluguel = mapResultSetToAluguel(rs);
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
                    alugueis.add(mapResultSetToAluguel(rs));
                }
            }
        } catch (SQLException e) {
            System.err
                    .println("Erro ao buscar alugueis por Cliente ID: " + clienteId + ". Detalhes: " + e.getMessage());
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
                    alugueis.add(mapResultSetToAluguel(rs));
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
    String sql = "INSERT INTO alugueis (cliente_id, carro_id, data_inicio, data_fim_prevista, " +
                 "status, valor_total) VALUES (?, ?, ?, ?, ?, ?)";

    Connection conn = null;
    try {
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false); // Inicia transação
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, aluguel.getClienteId());
            stmt.setLong(2, aluguel.getCarroId());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataInicio = LocalDate.parse(aluguel.getDataInicio(), formatter);
            LocalDate dataFimPrevista = LocalDate.parse(aluguel.getDataFimPrevista(), formatter);
            
            stmt.setDate(3, Date.valueOf(dataInicio));
            stmt.setDate(4, Date.valueOf(dataFimPrevista));
            stmt.setString(5, StatusAluguel.PENDENTE.name());
            stmt.setBigDecimal(6, aluguel.getValorTotal());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    aluguel.setId(rs.getLong(1));
                }
            }
            
            // Atualizar status do carro para ALUGADO
            String sqlUpdateCarro = "UPDATE carros SET status = ? WHERE id = ?";
            try (PreparedStatement stmtCarro = conn.prepareStatement(sqlUpdateCarro)) {
                stmtCarro.setString(1, StatusCarro.ALUGADO.name());
                stmtCarro.setLong(2, aluguel.getCarroId());
                stmtCarro.executeUpdate();
            }
            
            conn.commit(); // Confirma a transação
        }

    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback(); // Desfaz tudo em caso de erro
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        System.err.println("Erro ao inserir aluguel. Detalhes: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    // ------------------------------------
    // UPDATE - Processar aluguel (aprovar ou rejeitar)
    // ------------------------------------
    public void processarAluguel(Long id, boolean aprovar, String motivoRejeicao) {
    Connection conn = null;
    try {
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false); // Inicia transação
        
        // Buscar o aluguel para pegar o carro_id
        Aluguel aluguel = buscarPorId(id);
        
        String sql;
        if (aprovar) {
            sql = "UPDATE alugueis SET status = ?, data_aprovacao = ? WHERE id = ?";
        } else {
            sql = "UPDATE alugueis SET status = ?, motivo_rejeicao = ? WHERE id = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (aprovar) {
                stmt.setString(1, StatusAluguel.APROVADO.name());
                stmt.setTimestamp(2, GlobalBrDate.now());
                stmt.setLong(3, id);
                // Carro continua ALUGADO
            } else {
                stmt.setString(1, StatusAluguel.REJEITADO.name());
                stmt.setString(2, motivoRejeicao);
                stmt.setLong(3, id);
                
                // Se rejeitado, liberar o carro
                String sqlUpdateCarro = "UPDATE carros SET status = ? WHERE id = ?";
                try (PreparedStatement stmtCarro = conn.prepareStatement(sqlUpdateCarro)) {
                    stmtCarro.setString(1, StatusCarro.DISPONIVEL.name());
                    stmtCarro.setLong(2, aluguel.getCarroId());
                    stmtCarro.executeUpdate();
                }
            }

            stmt.executeUpdate();
            conn.commit(); // Confirma a transação
        }

    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        System.err.println("Erro ao processar aluguel ID: " + id + ". Detalhes: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    // ------------------------------------
    // UPDATE - Devolver carro
    // ------------------------------------
    public void devolver(Long id) {
    Connection conn = null;
    try {
        conn = ConnectionFactory.getConnection();
        conn.setAutoCommit(false); // Inicia transação
        
        Aluguel aluguel = buscarPorId(id);
        LocalDate hoje = LocalDate.now();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataFim = LocalDate.parse(aluguel.getDataFimPrevista(), formatter);
        
        int diasAtraso = 0;
        if (hoje.isAfter(dataFim)) {
            diasAtraso = (int) java.time.temporal.ChronoUnit.DAYS.between(dataFim, hoje);
        }
        
        String sql = "UPDATE alugueis SET status = ?, data_devolucao_real = ?, dias_atraso = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, StatusAluguel.DEVOLVIDO.name());
            stmt.setTimestamp(2, GlobalBrDate.now());
            stmt.setInt(3, diasAtraso);
            stmt.setLong(4, id);
            stmt.executeUpdate();
        }
        
        // Liberar o carro para DISPONIVEL
        String sqlUpdateCarro = "UPDATE carros SET status = ? WHERE id = ?";
        try (PreparedStatement stmtCarro = conn.prepareStatement(sqlUpdateCarro)) {
            stmtCarro.setString(1, StatusCarro.DISPONIVEL.name());
            stmtCarro.setLong(2, aluguel.getCarroId());
            stmtCarro.executeUpdate();
        }
        
        conn.commit(); // Confirma a transação

    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        System.err.println("Erro ao devolver aluguel ID: " + id + ". Detalhes: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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