var User = require('../models/user');
var MachineSubCategory = require('../models/machineSubCategory');
var Q = require("q");



var addNewMachineSubCategory = function(machinesubcategory) {
	var deferred = Q.defer();
	MachineSubCategory.findOne({'machinesubcategory' : machinesubcategory}, function(err, machinesubcategory) {
		// console.log(err);
		// console.log(machinesubcategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machinesubcategory) {
			console.log("machine sub category already exist = ", machinesubcategory);
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
	MachineSubCategory.find({}, function(err, machinesubcategorys) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(machinesubcategorys && machinesubcategorys != "undefined") {
			console.log("Got the Machine Sub-Category");
			deferred.resolve({
				'machinesubcategorys' : machinesubcategorys,
				status : 'success',
			});
		}
		else 
		{
			console.log("Machine Sub-Category not found");
			deferred.resolve({
				'machinesubcategorys' : null,
				status : 'machineSubCategoryNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachineSubCategory = function(id){
	var deferred = Q.defer();
	MachineSubCategory.remove({'_id' : id}, function(err, machinesubcategory) {
		console.log("machinesubcategory = ", machinesubcategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machinesubcategory && machinesubcategory.result.n >= 1) {
			console.log("Deleted Machine Sub-Category = ",machinesubcategory.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(machinesubcategory && machinesubcategory.result.n == 0)
		{
			console.log("Machine Sub-Category not found");
			deferred.resolve({
				status : 'noMachineSubCategoryFound',
			});
		}
	});
	return deferred.promise;
}


module.exports = {addNewMachineSubCategory : addNewMachineSubCategory,
			  	  listAllMachineSubCategory : listAllMachineSubCategory,
			  	  deleteMachineSubCategory : deleteMachineSubCategory
			  	};
