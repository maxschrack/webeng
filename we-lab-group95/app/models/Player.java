package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.h2.engine.Database;
import org.h2.engine.User;

import controllers.Application;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;

@Entity
public class Player implements at.ac.tuwien.big.we14.lab2.api.User{

	public enum Geschlecht {
		maennlich, weiblich
	}
	
	private String vorname;
	private String nachname;
	private String geburtsdatum;
	private Geschlecht geschlecht;
	@Id
	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(8)
	private String benutzername;
	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(8)
	private String passwort;
	
	public Player() {
		
	}
	
	public Player(String vorname, String nachname, String geburtsdatum,
			Geschlecht geschlecht, String benutzername, String passwort) {
		this.vorname = vorname;
		this.nachname = nachname;
		this.geburtsdatum = geburtsdatum;
		this.geschlecht = geschlecht;
		this.benutzername = benutzername;
		this.passwort = passwort;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(String geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getBenutzername() {
		return benutzername;
	}

	public void setBenutzername(String benutzername) {
		this.benutzername = benutzername;
	}

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public String getName() {
		return benutzername;
	}

	public void setName(String name) {
		this.benutzername = name;
	}
	
	/*
	public List<ValidationError> validate() {
		List<ValidationError> errors = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
 
		try {
			// Wenn das GebDat nicht geparst werden kann, wird eine Exception geworfen
			Date date = dateFormat.parse(geburtsdatum);
			System.out.println(date);
 
		} catch (ParseException e) {
			errors = new ArrayList<ValidationError>();
			errors.add(new ValidationError("geburtsdatum", "Das Datum muss vom Forma DD/MM/YYYY sein!"));
		}
		return errors;
	}
	*/
}




