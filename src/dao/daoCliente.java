package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Cliente;
import util.ConnectionFactory;
import util.GlobalBrDate;

public class daoCliente {

    // ------------------------------------
    // READ
    // ------------------------------------
    public List<Cliente> buscarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, email, telefone, data_cadastro FROM clientes";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("cpf"),
                        GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));

                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar clientes: " + e.getMessage());
            e.printStackTrace();
        }
        return clientes;
    }

    // ------------------------------------
    // READ BY ID
    // ------------------------------------
    public Cliente buscarPorId(Long id) {
        Cliente cliente = null;
        String sql = "SELECT id, nome, cpf, email, telefone, data_cadastro FROM clientes WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("cpf"),
                            GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
        return cliente;
    }

    // ------------------------------------
    // READ BY EMAIL
    // ------------------------------------
    public Cliente buscarPorEmail(String email) {
        Cliente cliente = null;
        String sql = "SELECT id, nome, cpf, email, telefone, data_cadastro FROM clientes WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("cpf"),
                            GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por EMAIL: " + email + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }

        return cliente;
    }

    // ------------------------------------
    // READ BY CPF
    // ------------------------------------
    public Cliente buscarPorCpf(String cpf) {
        Cliente cliente = null;
        String sql = "SELECT id, nome, cpf, email, telefone, data_cadastro FROM clientes WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("cpf"),
                            GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por CPF: " + cpf + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }

        return cliente;
    }

    // ------------------------------------
    // READ BY TELEFONE
    // ------------------------------------
    public Cliente buscarPorTelefone(String telefone) {
        Cliente cliente = null;
        String sql = "SELECT id, nome, cpf, email, telefone, data_cadastro FROM clientes WHERE telefone = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, telefone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("cpf"),
                            GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por TELEFONE: " + telefone + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }

        return cliente;
    }

    // ------------------------------------
    // CREATE 
    // ------------------------------------
    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, email, cpf, telefone, senha, data_cadastro) VALUES (?,?,?,?,?,?)";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    System.out.println(cliente);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getSenha());
            stmt.setTimestamp(6, GlobalBrDate.now());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + cliente.getNome() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // UPDATE
    // ------------------------------------
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.setLong(4, cliente.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente ID: " + cliente.getId() + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------
    // DELETE
    // ------------------------------------
    public void deletar(Long id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente ID: " + id + ". Detalhes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
