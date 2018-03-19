var User = require('../models/user');
var Snag = require('../models/snag');
var Q = require("q");

var addNewSnag = function(machineid, category, subcategory, partname, description, inspector1, 
	functionaloperator, inspector2, inspector3, currentstatus) {
		var deferred = Q.defer();
		var newSnag = new Snag();
			newSnag.machineid = machineid;
			newSnag.category = category;
			newSnag.subcategory = subcategory;
			newSnag.partname = partname;
			newSnag.description = description;
			newSnag.inspector1 = inspector1;
			newSnag.functionaloperator = functionaloperator;
			newSnag.inspector2 = inspector2;
			newSnag.inspector3 = inspector3;
			newSnag.currentstatus = currentstatus;
			newSnag.save(function(err) {
			if(err) {
				console.log("error is here!");
				deferred.reject(new Error(err));
			}
			else{
				console.log("success in dumping newSnag to db");
				deferred.resolve({
				'status' : 'success'
				});
			}	
		});

	    return deferred.promise;
};

var listAllSnag = function (){
	var  deferred = Q.defer();
	Snag.find({}, function(err, snags) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(snags && snags != "undefined") {
			console.log("Got the Snags");
			deferred.resolve({
				'snags' : snags,
				status : 'success',
			});
		}
		else 
		{
			console.log("snags not found");
			deferred.resolve({
				'snags' : null,
				status : 'snagsNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteSnag = function(id){
	var deferred = Q.defer();
	Snag.remove({'_id' : id}, function(err, snag) {
		console.log("snag = ", snag);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(snag && snag.result.n >= 1) {
			console.log("Deleted Snag = ", snag.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(snag && snag.result.n == 0)
		{
			console.log("Snag not found");
			deferred.resolve({
				status : 'noSnagFound',
			});
		}
	});
	return deferred.promise;
}

var updateSnag = function(id, description, inspector1, functionaloperator, inspector2, inspector3, currentstatus) {
	var deferred = Q.defer();
	Snag.findOneAndUpdate({'_id': id}, 
								{"$set" : {'description' : description, 
										  'inspector1' : inspector1,
										  'functionaloperator' : functionaloperator, 
										  'inspector2' : inspector2,
										  'inspector3' : inspector3,
										  'currentstatus' : currentstatus}},
								{new : true}, function(err, snag){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(snag){
	    	deferred.resolve({
	    		'snag' : snag,
	    		'status' : 'success',
	    		'updated' : true
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'snag' : null,
	    		'status' : 'noSnagFound',
	    		'updated' : false
	    	});
	    }
	 });
	return deferred.promise;
}

var getSnag = function(id) {
	var deferred = Q.defer();
	Snag.findOne({'_id' : id}, function(err, snag){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(snag && snag != "undefined"){
	    	deferred.resolve({
	    		'snag' : snag,
	    		'status' : 'success'
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'snag' : null,
	    		'status' : 'noSnagFound'
	    	});
	    }
	 });
	return deferred.promise;
}

module.exports = {addNewSnag : addNewSnag,
			  	  listAllSnag : listAllSnag,
			  	  deleteSnag : deleteSnag,
			  	  updateSnag : updateSnag,
			  	  getSnag : getSnag
			  	};
