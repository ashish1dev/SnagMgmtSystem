var User = require('../models/user');
var MachineCategory = require('../models/machineCategory');
var Q = require("q");


var addNewMachineCategory = function(machineCategory) {
	var deferred = Q.defer();
	MachineCategory.findOne({'machineCategory' : machineCategory}, function(err, categoryResponse) {
		// console.log(err);
		// console.log(machinecategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(categoryResponse) {
			console.log("machine category already exist = ", machineCategory);
			deferred.resolve({
				'status': 'machineCategoryAlreadyExist'
			});
		}
		else 
		{	
			var newMachineCategory = new MachineCategory();
			newMachineCategory.machineCategory = machineCategory;
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
	MachineCategory.find({}, function(err, machineCategorys) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(machineCategorys && machineCategorys != "undefined") {
			console.log("Got the Machine Category");
			deferred.resolve({
				'machineCategorys' : machineCategorys,
				status : 'success',
			});
		}
		else 
		{
			console.log("Machine Category not found");
			deferred.resolve({
				'machineCategorys' : null,
				status : 'machineCategoryNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachineCategory = function(id){
	var deferred = Q.defer();
	MachineCategory.remove({'_id' : id}, function(err, machineCategory) {
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machineCategory && machineCategory.result.n >= 1) {
			console.log("Deleted Machine Category = ", machineCategory.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(machineCategory && machineCategory.result.n == 0)
		{
			console.log("Machine Category not found");
			deferred.resolve({
				status : 'noMachineCategoryFound',
			});
		}
	});
	return deferred.promise;
}

module.exports = {addNewMachineCategory : addNewMachineCategory,
			  	  listAllMachineCategory : listAllMachineCategory,
			  	  deleteMachineCategory : deleteMachineCategory
			  	};
