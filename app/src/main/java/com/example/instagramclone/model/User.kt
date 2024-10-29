package com.example.instagramclone.model

class User{

    var image: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null

    // constructors
    constructor()   // empty constructor for firebase
    constructor(name: String?, email: String?, password:String?) {  // if the user does not provide an image but everything else
        this.name = name
        this.email = email
        this.password = password
    }
    constructor(name:String?, email:String?, password: String?, imageLink:String?) {     // all the fields
        this.name = name
        this.email = email
        this.password = password
        this.image = imageLink
    }
}