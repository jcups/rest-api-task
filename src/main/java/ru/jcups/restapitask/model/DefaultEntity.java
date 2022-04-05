package ru.jcups.restapitask.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties("new")
@MappedSuperclass
public abstract class DefaultEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	public boolean isNew() {
		return id != 0;
	}

	public abstract void refresh(DefaultEntity entity);
}
