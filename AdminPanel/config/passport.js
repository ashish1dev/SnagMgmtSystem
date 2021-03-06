// config/passport.js

// load all the things we need
var LocalStrategy   = require('passport-local').Strategy;

// load up the user model
var User            = require('../models/user');

// expose this function to our app using module.exports
module.exports = function(passport) {

    // =========================================================================
    // passport session setup ==================================================
    // =========================================================================
    // required for persistent login sessions
    // passport needs ability to serialize and unserialize users out of session

    // used to serialize the user for the session
    passport.serializeUser(function(user, done) {
        done(null, user.id);
    });

    // used to deserialize the user
    passport.deserializeUser(function(id, done) {
        User.findById(id, function(err, user) {
            done(err, user);
        });
    });


      // =========================================================================
      // LOCAL LOGIN =============================================================
      // =========================================================================
      passport.use('local-login', new LocalStrategy({
          // by default, local strategy uses username and password, we will override with username
          usernameField : 'username',
          passwordField : 'password',
          passReqToCallback : true // allows us to pass in the req from our route (lets us check if a user is logged in or not)
      },
      function(req, username, password, done) {
          if (username)
              username = username.toLowerCase(); // Use lower-case e-mails to avoid case-sensitive e-mail matching

          // asynchronous
          process.nextTick(function() {
              User.findOne({ 'local.username' :  username }, function(err, user) {
                  // if there are any errors, return the error
                  if (err)
                      return done(err);

                  // if no user is found, return the message
                  if (!user)
                      return done(null, false, req.flash('loginMessage', 'No user found.'));

                  if (!user.validPassword(password))
                      return done(null, false, req.flash('loginMessage', 'Oops! Wrong password.'));

                  // all is well, return user
                  else
                      return done(null, user);
              });
          });

      }));

    // =========================================================================
    // LOCAL SIGNUP ============================================================
    // =========================================================================
    // we are using named strategies since we have one for login and one for signup
    // by default, if there was no name, it would just be called 'local'

    passport.use('local-signup', new LocalStrategy({
        // by default, local strategy uses username and password, we will override with username
        firstnameField  : 'firstname',
        lastnameField : 'lastname',
        usernameField : 'username',
        passwordField : 'password',
        plainpasswordField  : 'plainpassword',
        passReqToCallback : true // allows us to pass back the entire request to the callback
    },
    function(req, username, password,done) {

        // asynchronous
        // User.findOne wont fire unless data is sent back
        console.log("req signup = ", req.body);

        process.nextTick(function() {

          // find a user whose username is the same as the forms username
          // we are checking to see if the user trying to login already exists
          User.findOne({ 'local.username' :  username }, function(err, user) {
              // if there are any errors, return the error
              if (err){
                  console.log(err);
                  console.log("pass");
                  return done(err);
              }
              // check to see if theres already a user with that username
              if (user) {
                  console.log("found same user");
                  return done(null, false, req.flash('signupMessage', 'That username is already taken.'));
              } else {

                  // if there is no user with that username
                  // create the user
                  var newUser            = new User();

                  // set the user's local credentials
                  newUser.local.firstname = req.body.firstname;
                  newUser.local.lastname = req.body.lastname;
                  newUser.local.username = req.body.username;
                  newUser.local.usertype = req.body.usertype;
                  newUser.local.password = newUser.generateHash(password);
                  newUser.local.plainpassword = req.body.plainpassword;

                  // save the user
                  newUser.save(function(err) {
                      if (err)
                          throw err;
                      return done(null, newUser);
                  });
              }

          });

        });

    }));

};
