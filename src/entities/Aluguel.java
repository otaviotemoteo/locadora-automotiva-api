package entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Aluguel {
    private Long id;
    private Long clienteId;
    private Long carroId;
    private String dataSolicitacao;
    private String dataAprovacao;
    private String dataInicio;
    private String dataFimPrevista;
    private String dataDevolucaoReal;
    private StatusAluguel status;
    private int diasAtraso;
    private BigDecimal valorTotal;
    private String motivoRejeicao;
    
    // Enum para o status do aluguel
    public enum StatusAluguel {
        PENDENTE,
        APROVADO,
        REJEITADO,
        DEVOLVIDO
    }
    
    // Construtores
    public Aluguel() {
        this.status = StatusAluguel.PENDENTE;
        this.diasAtraso = 0;
    }
    
    public Aluguel(Long clienteId, Long carroId, String dataInicio) {
        this.clienteId = clienteId;
        this.carroId = carroId;
        this.dataInicio = dataInicio;
        LocalDate dataInicioLD = LocalDate.parse(dataInicio);
        this.dataFimPrevista = dataInicioLD.plusDays(5).toString(); // Limite de 5 dias
        this.status = StatusAluguel.PENDENTE;
        this.diasAtraso = 0;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public Long getCarroId() {
        return carroId;
    }
    
    public void setCarroId(Long carroId) {
        this.carroId = carroId;
    }
    
    public String getDataSolicitacao() {
        return dataSolicitacao;
    }
    
    public void setDataSolicitacao(String dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
    
    public String getDataAprovacao() {
        return dataAprovacao;
    }
    
    public void setDataAprovacao(String dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }
    
    public String getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
        LocalDate dataInicioLD = LocalDate.parse(dataInicio);
        this.dataFimPrevista = dataInicioLD.plusDays(5).toString();
    }
    
    public String getDataFimPrevista() {
        return dataFimPrevista;
    }
    
    public void setDataFimPrevista(String dataFimPrevista) {
        this.dataFimPrevista = dataFimPrevista;
    }
    
    public String getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }
    
    public void setDataDevolucaoReal(String dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }
    
    public StatusAluguel getStatus() {
        return status;
    }
    
    public void setStatus(StatusAluguel status) {
        this.status = status;
    }
    
    public int getDiasAtraso() {
        return diasAtraso;
    }
    
    public void setDiasAtraso(int diasAtraso) {
        this.diasAtraso = diasAtraso;
    }
    
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }
    
    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
    
    public boolean estaAtrasado() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataFim = LocalDate.parse(dataFimPrevista);
        
        if (dataDevolucaoReal == null && hoje.isAfter(dataFim)) {
            return true;
        }
        if (dataDevolucaoReal != null) {
            LocalDate dataDevolucao = LocalDate.parse(dataDevolucaoReal);
            return dataDevolucao.isAfter(dataFim);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Aluguel{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", carroId=" + carroId +
                ", dataInicio=" + dataInicio +
                ", dataFimPrevista=" + dataFimPrevista +
                ", status=" + status +
                ", diasAtraso=" + diasAtraso +
                '}';
    }
}