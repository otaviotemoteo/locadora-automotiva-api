package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.Carro;
import entities.Carro.StatusCarro;
import util.ConnectionFactory;
import util.GlobalBrDate;

public class daoCarro {

  // ------------------------------------
  // READ
  // ------------------------------------
  public List<Carro> buscarTodos() {
    List<Carro> carros = new ArrayList<>();
    String sql = "SELECT id, placa, modelo, marca, ano, cor, valor_diaria, status, data_cadastro FROM carros";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {

        Carro carro = new Carro(
            rs.getLong("id"),
            rs.getString("placa"),
            rs.getString("modelo"),
            rs.getString("marca"),
            rs.getInt("ano"),
            rs.getString("cor"),
            rs.getBigDecimal("valor_diaria"),
            Carro.StatusCarro.valueOf(rs.getString("status")),
            GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));

        carros.add(carro);
      }

    } catch (SQLException e) {
      System.err.println("Erro ao buscar carros: " + e.getMessage());
      e.printStackTrace();
    }

    return carros;
  }

  // ------------------------------------
  // READ BY ID
  // ------------------------------------
  public Carro buscarPorId(Long id) {
    Carro carro = null;
    String sql = "SELECT id, placa, modelo, marca, ano, cor, valor_diaria, status, data_cadastro FROM carros WHERE id = ?";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {

          carro = new Carro(
              rs.getLong("id"),
              rs.getString("placa"),
              rs.getString("modelo"),
              rs.getString("marca"),
              rs.getInt("ano"),
              rs.getString("cor"),
              rs.getBigDecimal("valor_diaria"),
              Carro.StatusCarro.valueOf(rs.getString("status")),
              GlobalBrDate.formatTimestamp(rs.getTimestamp("data_cadastro")));
        }
      }

    } catch (SQLException e) {
      System.err.println("Erro ao buscar carro por ID: " + id + ". Detalhes: " + e.getMessage());
      e.printStackTrace();
    }

    return carro;
  }

  // ------------------------------------
// UPDATE - Atualizar status do carro
// ------------------------------------
public void atualizarStatus(Long id, StatusCarro novoStatus) {
    String sql = "UPDATE carros SET status = ? WHERE id = ?";
    
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, novoStatus.name());
        stmt.setLong(2, id);
        
        stmt.executeUpdate();
        
    } catch (SQLException e) {
        System.err.println("Erro ao atualizar status do carro ID: " + id + ". Detalhes: " + e.getMessage());
        e.printStackTrace();
    }
}
}
