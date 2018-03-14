var User = require('../models/user');
var MobileUser = require('../models/mobileUser');
var Q = require("q");

// route middleware to make sure a user is logged in
var isLoggedIn = function(req, res, next) {
    // if user is authenticated in the session, carry on
    if (req.isAuthenticated())
      return next();

    // if they aren't redirect them to the home page
    res.redirect('/');
};

var addNewMobileUser = function(firstname, lastname, username, usertype, password) {
	var deferred = Q.defer();
	MobileUser.findOne({'username' : username}, function(err,user) {
		// console.log(err);
		// console.log(user);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(user) {
			console.log("user already exist = ",user);
			deferred.resolve({
				'status': 'userAlreadyExist'
			});
		}
		else 
		{	
			var newMobileUser = new MobileUser();
			newMobileUser.firstname = firstname;
			newMobileUser.lastname = lastname;
			newMobileUser.username = username;
			newMobileUser.usertype = usertype;
			newMobileUser.password = password;
			newMobileUser.save(function(err) {
				if(err) {
					deferred.reject(new Error(err));
				}
				else{
					console.log("success in dumping newMobileUser to db");
					deferred.resolve({
						'status' : 'success'
					});
				}	
		    });
		}
	});


    return deferred.promise;
};

var listAllMobileUsers = function (){
	var  deferred = Q.defer();
	MobileUser.find({}, function(err, users) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(users && users != "undefined") {
			console.log("Got the Mobile Users");
			deferred.resolve({
				'users' : users,
				status : 'success',
			});
		}
		else 
		{
			console.log("user not found");
			deferred.resolve({
				'users' : null,
				status : 'userNotFound',
			});
		}
	}).sort({
        "created": -1
    });;
	return deferred.promise;
}

var deleteMobileUser = function(username){
	var deferred = Q.defer();
	MobileUser.remove({'username' : username}, function(err, user) {
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		// console.log("user =",user);
		if(user && user.result.n >= 1) {
			console.log("Deleted User = ",user.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(user && user.result.n == 0)
		{
			console.log("user not found");
			deferred.resolve({
				status : 'noUserFound',
			});
		}
	});
	return deferred.promise;
}

module.exports = {isLoggedIn:isLoggedIn,
			      addNewMobileUser:addNewMobileUser,
			  	  listAllMobileUsers:listAllMobileUsers,
			  	  deleteMobileUser:deleteMobileUser
			  	};
