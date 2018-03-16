// load the things we need
var mongoose = require('mongoose');
var bcrypt   = require('bcrypt-nodejs');

// define the schema for our user model
var mobileUserSchema = mongoose.Schema({
      firstname : String,
      lastname  : String,
      username    : String,
      usertype : String,
      password    : String,
      profilepicture : { 
        data: Buffer, 
        contentType: String 
      },
      created: {
        type: Date,
        default: Date.now
      },

});


// create the model for users and expose it to our app
module.exports = mongoose.model('MobileUserCollection', mobileUserSchema);
