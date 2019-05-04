package com.divideandconquer.mylib;

public class Student {
	private String email;
	private String library_card_number;

	public Student(String e,String l) {
		email = String.valueOf(e);
		library_card_number = String.valueOf(l);
	}

	public String getEmail() {
		return email;
	}

	public String getLibrary_card_number() {
		return library_card_number;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLibrary_card_number(String library_card_number) {
		this.library_card_number = library_card_number;
	}
}
