var User = require('../models/user');
var MachineSubCategory = require('../models/machineSubCategory');
var Q = require("q");



var addNewMachineSubCategory = function(machineSubCategory) {
	var deferred = Q.defer();
	MachineSubCategory.findOne({'machineSubCategory' : machineSubCategory}, function(err, machineSubCategory) {
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
			newMachineSubCategory.machineSubCategory = machineSubCategory;
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
	MachineSubCategory.find({}, function(err, machineSubCategorys) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(machineSubCategorys && machineSubCategorys != "undefined") {
			console.log("Got the Machine Sub-Category");
			deferred.resolve({
				'machineSubCategorys' : machineSubCategorys,
				status : 'success',
			});
		}
		else 
		{
			console.log("Machine Sub-Category not found");
			deferred.resolve({
				'machineSubCategorys' : null,
				status : 'machineSubCategoryNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachineSubCategory = function(id){
	var deferred = Q.defer();
	MachineSubCategory.remove({'_id' : id}, function(err, machineSubCategory) {
		console.log("machineSubCategory = ", machineSubCategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machineSubCategory && machineSubCategory.result.n >= 1) {
			console.log("Deleted Machine Sub-Category = ",machinesubcategory.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(machineSubCategory && machineSubCategory.result.n == 0)
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
