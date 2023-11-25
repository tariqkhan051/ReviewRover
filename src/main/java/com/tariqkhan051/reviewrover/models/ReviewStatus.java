package com.tariqkhan051.reviewrover.models;

import jakarta.persistence.*;

@Entity
@Table(name = "review_status")
public class ReviewStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private EReviewStatus name;

	public ReviewStatus() {

	}

	public ReviewStatus(EReviewStatus name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EReviewStatus getName() {
		return name;
	}

	public void setName(EReviewStatus name) {
		this.name = name;
	}
}