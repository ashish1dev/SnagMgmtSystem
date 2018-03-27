var User = require('../models/user');
var MobileUser = require('../models/mobileUser');
var Q = require("q");



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
	});
	return deferred.promise;
}

var deleteMobileUser = function(username){
	var deferred = Q.defer();
	MobileUser.remove({'username' : username}, function(err, user) {
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
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

var updateMobileUser = function(firstname, lastname, username, usertype, password) {
	var deferred = Q.defer();
	MobileUser.findOneAndUpdate({'username': username}, 
								{"$set" : {'firstname' : firstname, 'lastname' : lastname, 'usertype' : usertype, 'password' : password}},
								{new : true}, function(err, user){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(user){
	    	deferred.resolve({
	    		'user' : user,
	    		'status' : 'success',
	    		'updated' : true
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'user' : null,
	    		'status' : 'noUserFound',
	    		'updated' : false
	    	});
	    }
	 });
	return deferred.promise;
}

var getMobileUser = function(username) {
	var deferred = Q.defer();
	MobileUser.findOne({'username' : username}, function(err,user){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(user && user != "undefined"){
	    	deferred.resolve({
	    		'user' : user,
	    		'status' : 'success'
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'user' : null,
	    		'status' : 'noUserFound'
	    	});
	    }
	 });
	return deferred.promise;
}

var authenticateMobileUser = function(username, password) {
	var deferred = Q.defer();
	MobileUser.find({'username' : username, 'password' : password}, function(err,user) {
		console.log("user authenticateMobileUser = ", user);
		if(err) {
			deferred.reject(new Error(err));
		}
		if(user && user.length == 0){
			deferred.resolve({
				'user' : null,
				'status' : 'noUserFound'
			});
		}
		else if(user && user.length >=1){
			deferred.resolve({
				'user' : user[0],
				'status' : 'success'
			});
		}
	});
	return deferred.promise;
}


module.exports = {addNewMobileUser : addNewMobileUser,
			  	  listAllMobileUsers : listAllMobileUsers,
			  	  deleteMobileUser : deleteMobileUser,
			  	  updateMobileUser : updateMobileUser,
			  	  getMobileUser : getMobileUser,
			  	  authenticateMobileUser : authenticateMobileUser
			  	};
