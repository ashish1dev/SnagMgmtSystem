var User = require('../models/user');
var MachineCategory = require('../models/machineCategory');
var Q = require("q");



var addNewMachineCategory = function(machinecategory) {
	var deferred = Q.defer();
	MachineCategory.findOne({'machinecategory' : machinecategory}, function(err, user) {
		// console.log(err);
		// console.log(user);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(user) {
			console.log("machine category already exist = ",user);
			deferred.resolve({
				'status': 'machineCategoryAlreadyExist'
			});
		}
		else 
		{	
			var newMachineCategory = new MachineCategory();
			newMachineCategory.machinecategory = machinecategory;
			newMachineCategory.save(function(err) {
				if(err) {
					deferred.reject(new Error(err));
				}
				else{
					console.log("success in dumping newMachineCategory to db");
					deferred.resolve({
						'status' : 'success'
					});
				}	
		    });
		}
	});


    return deferred.promise;
};

var listAllMachineCategory = function (){
	var  deferred = Q.defer();
	MachineCategory.find({}, function(err, users) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(users && users != "undefined") {
			console.log("Got the Machine Category");
			deferred.resolve({
				'users' : users,
				status : 'success',
			});
		}
		else 
		{
			console.log("Machine Category not found");
			deferred.resolve({
				'users' : null,
				status : 'machineCategoryNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachineCategory = function(id){
	var deferred = Q.defer();
	MachineCategory.remove({'_id' : id}, function(err, user) {
		console.log("user = ", user);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(user && user.result.n >= 1) {
			console.log("Deleted Machine Category = ",user.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(user && user.result.n == 0)
		{
			console.log("Machine Category not found");
			deferred.resolve({
				status : 'noMachineCategoryFound',
			});
		}
	});
	return deferred.promise;
}


//temproraliy not using the update finction
// var updateMachineCategory = function(id) {
// 	var deferred = Q.defer();
// 	MachineCategory.findOneAndUpdate({'_id': id}, 
// 								{"$set" : {'machinecategory' : machinecategory}},
// 								{new : true}, function(err, user){
// 		if (err) {
// 	      deferred.reject(new Error(err));
// 	    }
// 	    if(user){
// 	    	deferred.resolve({
// 	    		'user' : user,
// 	    		'status' : 'success',
// 	    		'updated' : true
// 	    	});
// 	    }
// 	    else{
// 	    	deferred.resolve({
// 	    		'user' : null,
// 	    		'status' : 'noMachineCategoryFound',
// 	    		'updated' : false
// 	    	});
// 	    }
// 	 });
// 	return deferred.promise;
// }

// var getMachineCategory = function(id) {
// 	var deferred = Q.defer();
// 	MachineCategory.findOne({'_id' : id}, function(err, user){
// 		if (err) {
// 	      deferred.reject(new Error(err));
// 	    }
// 	    if(user && user != "undefined"){
// 	    	deferred.resolve({
// 	    		'user' : user,
// 	    		'status' : 'success'
// 	    	});
// 	    }
// 	    else{
// 	    	deferred.resolve({
// 	    		'user' : null,
// 	    		'status' : 'noMachineCategoryFound'
// 	    	});
// 	    }
// 	 });
// 	return deferred.promise;
// }


module.exports = {addNewMachineCategory : addNewMachineCategory,
			  	  listAllMachineCategory : listAllMachineCategory,
			  	  deleteMachineCategory : deleteMachineCategory
			  	};
