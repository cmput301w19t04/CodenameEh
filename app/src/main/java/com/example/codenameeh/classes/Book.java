package com.example.codenameeh.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.Nullable;

/**
 * Requested, borrowed, accepted, denied by other users.
 * Represents a real life book through representation of title, uuid,
 * author, ISBN, decription, optional photograph, owner, keywords.
 * Keeps track of who to is requesting this book, as well as if it's borrowed,
 * accepted, confirmed to transact, etc.
 * ownerConfirmation and borrowerConfirmation are used for the transaction of
 * borrowing and returning
 */
public class Book implements Parcelable {
    private String uuid;
    private String title;
    private String author;
    private String ISBN;
    private String description;
    private String photograph; //filename of the image in database
    private String owner;
    private String borrower;
    private ArrayList<String> requestedBy;
    private boolean borrowed;
    private boolean acceptedStatus;
    private ArrayList<String> keywords;
    private boolean ownerConfirmation;
    private boolean borrowerConfirmation;


    // empty constructor for serial reconstruction
    public Book (){
        this.ownerConfirmation = false;
        this.borrowerConfirmation = false;
        this.borrower = null;
    }

    /**
     * A photo is provided for the construction of the Book object
     * @param title
     * @param author
     * @param ISBN
     * @param description
     * @param photograph
     * @param Owner
     * @param keywords
     */
    public Book(String title, String author, String ISBN, String description, String photograph, String Owner, ArrayList<String> keywords) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.description = description;
        this.photograph = photograph;
        this.borrowed = false;
        this.owner = Owner;
        this.requestedBy = new ArrayList<>();
        this.acceptedStatus = false;
        this.uuid = UUID.randomUUID().toString();
        this.keywords = keywords;
        this.ownerConfirmation = false;
        this.borrowerConfirmation = false;
        this.borrower = null;

    }

    /**
     * No photograph is provided for the construction of the Book object
     * @param title
     * @param author
     * @param ISBN
     * @param description
     * @param Owner
     * @param keywords
     */
    public Book(String title, String author, String ISBN, String description, String Owner, ArrayList<String> keywords) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.description = description;
        this.photograph = null;
        this.borrowed = false;
        this.owner = Owner;
        this.requestedBy = new ArrayList<>();
        this.acceptedStatus = false;
        this.uuid = UUID.randomUUID().toString();
        this.keywords = keywords;
        this.ownerConfirmation = false;
        this.borrowerConfirmation = false;
        this.borrower = null;
    }

    public boolean getOwnerConfirmation() {
        return ownerConfirmation;
    }

    public void setOwnerConfirmation(boolean ownerConfirmation) {
        this.ownerConfirmation = ownerConfirmation;
    }
    public boolean isConfirmed(){
        return this.ownerConfirmation&&this.borrowerConfirmation;
    }
    public boolean getBorrowerConfirmation() {
        return borrowerConfirmation;
    }

    public void setBorrowerConfirmation(boolean borrowerConfirmation) {
        this.borrowerConfirmation = borrowerConfirmation;
    }



    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    /**
     * When a book is borrowed, it is no longer accepted, the book now knows who
     * borrowed it, and the transaction is invalid once again.
     * @param borrower
     */
    public void borrow(User borrower){
        this.acceptedStatus = false;
        this.borrower = borrower.getUsername();
        this.borrowed = true;
        this.ownerConfirmation = false;
        this.borrowerConfirmation = false;
    }

    /**
     * When a book is unborrowed, the book removes what borrower it had, and the
     * transaction is invalid once again.
     */
    public void unborrow(){
        this.borrowed = false;
        this.borrower = null;
        this.ownerConfirmation = false;
        this.borrowerConfirmation = false;
    }


    public void addRequest(String user){
        this.requestedBy.add(user);
    }

    public void removeAllRequests(){
        this.requestedBy.clear();
    }

    public ArrayList<String> getRequestedBy(){
        return (ArrayList<String>) this.requestedBy.clone();
    }

    public void removeRequest(String user){
        this.requestedBy.remove(user);
    }

    public void setOwner(String owner){
        this.owner = owner;
    }
    public String getOwner(){
        return this.owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotograph() {
        return photograph;
    }

    public void setPhotograph(String photograph) {
        this.photograph = photograph;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public void setAcceptedStatus(boolean status){this.acceptedStatus = status;}
    public boolean getAcceptedStatus(){return this.acceptedStatus;}

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Compares a book with another book object properly
     * @param obj
     * @return
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(this==obj){
            return true;
        } else if(obj==null){
            return false;
        } else{
            Book other;
            try {
                other = (Book) obj;
            } catch(Exception e){
                return false;
            }
            return (this.uuid.equals(other.getUuid()));
        }
    }

    /**
     * When displayed in several activities, displays relevant information
     * @return representative string of the book
     */
    @Override
    public String toString() {
        String output = "Title: "+this.title+ "\t\tAuthor: "+ this.author
                + "\t\t ISBN: " + this.ISBN;
        if (!this.description.equals("")) {
            if (this.description.length() < 80) {
                output = output + "\nDescription: " + this.description;
            }
            else {
                output = output + "\nDescription: " + this.description.substring(0, 79) + "...";
            }
        }
        if (this.borrowed) {
            output = output + "\nCURRENTLY BORROWED";
        }
        else if (this.acceptedStatus){
            output = output + "\nCURRENTLY ACCEPTED";
        }
        else if (this.requestedBy.isEmpty()) {
            output = output + "\nCURRENTLY AVAILABLE";
        }
        else {
            output = output + "\nHAS PENDING REQUESTS";
        }
        return (output);
    }

    /**
     * Used when passing through to other activities via intent.
     * Preserves book information to be received by other activities.
     * @param in
     */
    protected Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        ISBN = in.readString();
        description = in.readString();
        photograph = in.readString();
        owner =  in.readString();

        uuid = in.readString();

        if (in.readByte() == 0x01) {
            requestedBy = new ArrayList<String>();
            in.readList(requestedBy, String.class.getClassLoader());
        } else {
            requestedBy = null;
        }
        borrowed = in.readByte() != 0x00;
        acceptedStatus = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes relevant data to intent using Parcelable to be read by
     * other activities
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(ISBN);
        dest.writeString(description);
        dest.writeString(photograph);
        dest.writeString(owner);

        dest.writeString(uuid);

        if (requestedBy == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(requestedBy);
        }
        dest.writeByte((byte) (borrowed ? 0x01 : 0x00));
        dest.writeByte((byte) (acceptedStatus ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}