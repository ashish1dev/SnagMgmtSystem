var User = require('../models/user');
var Machine = require('../models/machine');
var Q = require("q");



var addNewMachine = function(modelname) {
	var deferred = Q.defer();
	var newMachine = new Machine();
		newMachine.modelname = modelname;
		newMachine.save(function(err) {
		if(err) {
			deferred.reject(new Error(err));
		}
		else{
			console.log("success in dumping newMachine to db");
			deferred.resolve({
			'status' : 'success'
			});
		}	
	});

    return deferred.promise;
};

var listAllMachine = function (){
	var  deferred = Q.defer();
	Machine.find({}, function(err, users) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(users && users != "undefined") {
			console.log("Got the Machine");
			deferred.resolve({
				'users' : users,
				status : 'success',
			});
		}
		else 
		{
			console.log("machine not found");
			deferred.resolve({
				'users' : null,
				status : 'machineNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachine = function(id){
	var deferred = Q.defer();
	Machine.remove({'_id' : id}, function(err, user) {
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

var updateMachine = function(id, modelname) {
	var deferred = Q.defer();
	Machine.findOneAndUpdate({'_id': id}, 
								{"$set" : {'modelname' : modelname}},
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

var getMachine = function(id) {
	var deferred = Q.defer();
	Machine.findOne({'_id' : id}, function(err,user){
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


module.exports = {addNewMachine : addNewMachine,
			  	  listAllMachine : listAllMachine,
			  	  deleteMachine : deleteMachine,
			  	  updateMachine : updateMachine,
			  	  getMachine : getMachine
			  	};
