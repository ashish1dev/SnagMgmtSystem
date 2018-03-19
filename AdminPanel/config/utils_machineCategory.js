var User = require('../models/user');
var MachineCategory = require('../models/machineCategory');
var Q = require("q");



var addNewMachineCategory = function(machinecategory) {
	var deferred = Q.defer();
	MachineCategory.findOne({'machinecategory' : machinecategory}, function(err, machinecategory) {
		// console.log(err);
		// console.log(machinecategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machinecategory) {
			console.log("machine category already exist = ", machinecategory);
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
	MachineCategory.find({}, function(err, machinecategorys) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(machinecategorys && machinecategorys != "undefined") {
			console.log("Got the Machine Category");
			deferred.resolve({
				'machinecategorys' : machinecategorys,
				status : 'success',
			});
		}
		else 
		{
			console.log("Machine Category not found");
			deferred.resolve({
				'machinecategorys' : null,
				status : 'machineCategoryNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachineCategory = function(id){
	var deferred = Q.defer();
	MachineCategory.remove({'_id' : id}, function(err, machinecategory) {
		console.log("machinecategory = ", machinecategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machinecategory && machinecategory.result.n >= 1) {
			console.log("Deleted Machine Category = ", machinecategory.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(machinecategory && machinecategory.result.n == 0)
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
