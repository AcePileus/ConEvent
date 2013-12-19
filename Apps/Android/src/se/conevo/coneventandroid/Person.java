package se.conevo.coneventandroid;

public class Person
{
    public String Email;
    public String FirstName;
    public String LastName;
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return FirstName + " " + LastName;
    }
}