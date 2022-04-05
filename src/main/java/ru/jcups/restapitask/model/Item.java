package ru.jcups.restapitask.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "items")
public class Item extends DefaultEntity {

	@Size(min = 3, max = 64, message = "Name length should be between 3 and 64 chars")
	@Column(nullable = false)
	String title;

	File titleImage;

	@Positive(message = "Price cannot be negative")
	BigDecimal price;

	@Size(min = 16, max = 1024, message = "Description length should be between 16 and 1024 chars")
	@Column(nullable = false)
	String description;

	@Override
	public void refresh(DefaultEntity entity) {
		Item item = (Item) entity;
		if (item.title != null)
			this.title = item.title;
		if (item.titleImage != null)
			this.titleImage = item.titleImage;
		if (item.price != null)
			this.price = item.price;
		if (item.description != null)
			this.description = item.description;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@Builder
	@Entity(name = "items_info")
	public static class Info extends DefaultEntity {

		String brand;

		String model;

		String series;

		@ElementCollection(fetch = FetchType.EAGER)
		Set<File> images;

		@OneToMany
		List<ParamGroup> params;

		@Override
		public void refresh(DefaultEntity entity) {
			Info info = (Info) entity;
			if (info.brand != null)
				this.brand = info.brand;
			if (info.model != null)
				this.model = info.model;
			if (info.series != null)
				this.series = info.series;
			if (info.images != null)
				this.images = info.images;
			if (info.params != null)
				this.params = info.params;
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@FieldDefaults(level = AccessLevel.PRIVATE)
	@Builder
	@Entity(name = "item_params_group")
	public static class ParamGroup extends DefaultEntity {
		String groupName;

		@OneToMany(mappedBy = "group")
		List<Param> paramList;

		@Override
		public void refresh(DefaultEntity entity) {
			ParamGroup group = (ParamGroup) entity;
			if (group.groupName != null)
				this.groupName = group.groupName;
			if (group.paramList != null)
				this.paramList = group.paramList;
		}

		@Getter
		@Setter
		@AllArgsConstructor
		@NoArgsConstructor
		@FieldDefaults(level = AccessLevel.PRIVATE)
		@Builder
		@Entity(name = "item_param")
		public static class Param extends DefaultEntity {
			@ManyToOne
			ParamGroup group;
			Class<?> valueType;
			String name;
			String value;

			public Param(String name, String value) {
				this.name = name;
				this.value = value;
				if (value.equals("true") || value.equals("false") || value.equals("+") || value.equals("-")) {
					this.valueType = Boolean.class;
				} else if (value.matches("\\D+")) {
					this.valueType = Integer.class;
				} else if (value.matches("[\\D+.]")) {
					this.valueType = Double.class;
				} else {
					this.valueType = String.class;
				}
			}

			@Override
			public void refresh(DefaultEntity entity) {
				Param param = (Param) entity;
				if (param.name != null)
					this.name = param.name;
				if (param.value != null)
					this.value = param.value;
				if (param.valueType != null)
					this.valueType = param.valueType;
			}
		}
	}
}
