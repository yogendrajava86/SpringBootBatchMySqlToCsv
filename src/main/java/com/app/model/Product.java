package com.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="prodstab")
public class Product {

	@Id
	@Column(name="pid")
	private Integer id;
	@Column(name="pcode")
	private String code;
	@Column(name="pcost")
	private Double cost;
	@Column(name="pdisc")
	private Double disc;
	@Column(name="pgst")
	private Double gst;
	
	public Product() {
		super();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public Double getDisc() {
		return disc;
	}
	public void setDisc(Double disc) {
		this.disc = disc;
	}
	public Double getGst() {
		return gst;
	}
	public void setGst(Double gst) {
		this.gst = gst;
	}
	@Override
	public String toString() {
		return "Product [id=" + id + ", code=" + code + ", cost=" + cost + ", disc=" + disc + ", gst=" + gst + "]";
	}
}
