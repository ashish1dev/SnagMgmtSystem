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
	Machine.find({}, function(err, machines) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(machines && machines != "undefined") {
			console.log("Got the Machine");
			deferred.resolve({
				'machines' : machines,
				status : 'success',
			});
		}
		else 
		{
			console.log("machine not found");
			deferred.resolve({
				'machines' : null,
				status : 'machineNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteMachine = function(id){
	var deferred = Q.defer();
	Machine.remove({'_id' : id}, function(err, machine) {
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(machine && machine.result.n >= 1) {
			console.log("Deleted Machine = ",machine.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(machine && machine.result.n == 0)
		{
			console.log("machine not found");
			deferred.resolve({
				status : 'noMachineFound',
			});
		}
	});
	return deferred.promise;
}

var updateMachine = function(id, modelname) {
	var deferred = Q.defer();
	Machine.findOneAndUpdate({'_id': id}, 
								{"$set" : {'modelname' : modelname}},
								{new : true}, function(err, machine){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(machine){
	    	deferred.resolve({
	    		'machine' : machine,
	    		'status' : 'success',
	    		'updated' : true
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'machine' : null,
	    		'status' : 'noMachineFound',
	    		'updated' : false
	    	});
	    }
	 });
	return deferred.promise;
}

var getMachine = function(id) {
	var deferred = Q.defer();
	Machine.findOne({'_id' : id}, function(err, machine){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(machine && machine != "undefined"){
	    	deferred.resolve({
	    		'machine' : machine,
	    		'status' : 'success'
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'machine' : null,
	    		'status' : 'noMachineFound'
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
