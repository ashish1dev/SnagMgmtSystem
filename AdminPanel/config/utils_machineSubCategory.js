var User = require('../models/user');
var MachineSubCategory = require('../models/machineSubCategory');
var Q = require("q");



var addNewMachineSubCategory = function(machinesubcategory) {
	var deferred = Q.defer();
	MachineSubCategory.findOne({'machinesubcategory' : machinesubcategory}, function(err, user) {
		// console.log(err);
		// console.log(user);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(user) {
			console.log("machine sub category already exist = ",user);
			deferred.resolve({
				'status': 'machineSubCategoryAlreadyExist'
			});
		}
		else 
		{	
			var newMachineSubCategory = new MachineSubCategory();
			newMachineSubCategory.machinesubcategory = machinesubcategory;
			newMachineSubCategory.save(function(err) {
				if(err) {
					deferred.reject(new Error(err));
				}
				else{
					console.log("success in dumping newMachineSubCategory to db");
					deferred.resolve({
						'status' : 'success'
					});
				}	
		    });
		}
	});


    return deferred.promise;
};

var listAllMachineSubCategory = function (){
	var  deferred = Q.defer();
	MachineSubCategory.find({}, function(err, users) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(users && users != "undefined") {
			console.log("Got the Machine Sub-Category");
			deferred.resolve({
				'users' : users,
				status : 'success',
			});
		}
		else 
		{
			console.log("Machine Sub-Category not found");
			deferred.resolve({
				'users' : null,
				status : 'machineSubCategoryNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachineSubCategory = function(id){
	var deferred = Q.defer();
	MachineSubCategory.remove({'_id' : id}, function(err, user) {
		console.log("user = ", user);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(user && user.result.n >= 1) {
			console.log("Deleted Machine Sub-Category = ",user.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(user && user.result.n == 0)
		{
			console.log("Machine Sub-Category not found");
			deferred.resolve({
				status : 'noMachineSubCategoryFound',
			});
		}
	});
	return deferred.promise;
}


//temproraliy not using the update finction
// var updateMachineSubCategory = function(id) {
// 	var deferred = Q.defer();
// 	MachineSubCategory.findOneAndUpdate({'_id': id}, 
// 								{"$set" : {'machinesubcategory' : machinesubcategory}},
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
// 	    		'status' : 'noMachineSubCategoryFound',
// 	    		'updated' : false
// 	    	});
// 	    }
// 	 });
// 	return deferred.promise;
// }

// var getMachineSubCategory = function(id) {
// 	var deferred = Q.defer();
// 	MachineSubCategory.findOne({'_id' : id}, function(err, user){
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
// 	    		'status' : 'noMachineSubCategoryFound'
// 	    	});
// 	    }
// 	 });
// 	return deferred.promise;
// }


module.exports = {addNewMachineSubCategory : addNewMachineSubCategory,
			  	  listAllMachineSubCategory : listAllMachineSubCategory,
			  	  deleteMachineSubCategory : deleteMachineSubCategory
			  	};
