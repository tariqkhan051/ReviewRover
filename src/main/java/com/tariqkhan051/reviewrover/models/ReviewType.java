package com.tariqkhan051.reviewrover.models;

import jakarta.persistence.*;

@Entity
@Table(name = "review_type")
public class ReviewType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private EReviewType name;

	public ReviewType() {

	}

	public ReviewType(EReviewType name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EReviewType getName() {
		return name;
	}

	public void setName(EReviewType name) {
		this.name = name;
	}
}