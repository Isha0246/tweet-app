package com.tweetapp.tweetappbackend.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "USER")
public class User {

	@Id
	@Min(1)
	public int userId;

	@NotBlank(message = "First Name cannot be null")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid First Name")
	public String firstName;

	@NotBlank(message = "Last Name cannot be null")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid Last Name")
	public String lastName;

	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid EmailId")
	@NotBlank(message = "Enter Valid Email Id")
	public String emailId;

	@NotBlank(message = "Password cannot be null")
	public String password;

	@NotBlank(message = "Contact Number cannot be null")
	@Pattern(regexp = "[1-9][0-9]{9}", message = "Invalid Contact Number")
	public String contactNumber;

}
