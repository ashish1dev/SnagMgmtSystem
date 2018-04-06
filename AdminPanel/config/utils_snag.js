var User = require('../models/user');
var Snag = require('../models/snag');
var Q = require("q");
var Category = require('../models/machineCategory');
var SubCategory = require('../models/machineSubCategory');
var Parts = require('../models/parts');


var addNewSnag = function(machineID, category, subCategory, partName, description, inspector1UserName, 
	functionalOperatorUserName, inspector2UserName, inspector3UserName, currentStatusOfSnag) {
		var deferred = Q.defer();
		var newSnag = new Snag();
			newSnag.machineID = machineID;
			newSnag.category = category;
			newSnag.subCategory = subCategory;
			newSnag.partName = partName;
			newSnag.description = description;
			newSnag.inspector1UserName = inspector1UserName;
			newSnag.functionalOperatorUserName = functionalOperatorUserName;
			newSnag.inspector2UserName = inspector2UserName;
			newSnag.inspector3UserName = inspector3UserName;
			newSnag.currentStatusOfSnag = currentStatusOfSnag;
			console.log("new snag", newSnag);
			newSnag.save(function(err, snag) {

			if(err) {
				console.log("error is here!");
				deferred.resolve({
					'status' : 'failed'
				});
			}
			else{
				console.log("success in dumping newSnag to db");
				deferred.resolve({
					'status' : 'success',
					'snagID' : snag.snagID
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
			console.log("snags", snags);
			deferred.resolve({
				'snags' : snags,
				'status' : 'success',
			});
		}
		else 
		{
			console.log("snags not found");
			deferred.resolve({
				'snags' : null,
				'status' : 'snagsNotFound',
			});
		}
	});
	return deferred.promise;
}

var listOfSnagBySnagType = function (currentStatusOfSnag){
	var  deferred = Q.defer();
	Snag.find({'currentStatusOfSnag' : currentStatusOfSnag}, function(err, snags) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(snags && snags != "undefined") {
			console.log("Got the Snags");
			console.log("snags", snags);
			deferred.resolve({
				'snags' : snags,
				'status' : 'success',
			});
		}
		else 
		{
			console.log("snags not found");
			deferred.resolve({
				'snags' : null,
				'status' : 'snagsNotFound',
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

var updateSnag = function(id, description, inspector1UserName, functionalOperatorUserName, inspector2UserName, inspector3UserName, currentStatusOfSnag) {
	var deferred = Q.defer();
	Snag.findOneAndUpdate({'_id': id}, 
								{"$set" : {'description' : description, 
										  'inspector1UserName' : inspector1UserName,
										  'functionalOperatorUserName' : functionalOperatorUserName, 
										  'inspector2UserName' : inspector2UserName,
										  'inspector3UserName' : inspector3UserName,
										  'currentStatusOfSnag' : currentStatusOfSnag}},
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

var updateCurrentStatus = function(snagID, userName, userType, currentStatusOfSnag) {
	var deferred = Q.defer();
	var setString = {};
	setString.currentStatusOfSnag = currentStatusOfSnag;
	if(userType == "FUNCTIONAL_OPERATOR"){
		setString.functionalOperatorUserName = userName;
	}
	if(userType == "INSPECTOR2"){
		setString.inspector2UserName = userName;
	}
	if(userType == "INSPECTOR3"){
		setString.inspector3UserName = userName;
	}
	console.log("setString = ",setString);
	Snag.findOneAndUpdate({'snagID' : snagID},
							{"$set" : setString}, function(err, snag){
		if (err) {
	      deferred.reject(new Error(err));
	    }
	    if(snag){
	    	deferred.resolve({
	    		'snag' : snag,
	    		'status' : 'success',
	    	});
	    }
	    else{
	    	deferred.resolve({
	    		'snag' : null,
	    		'status' : 'noSnagFound',
	    	});
	    }
	 });
	return deferred.promise;
}

var getAllCategorySubCategoryParts = function(id) {
	var deferred = Q.defer();
	var result = {};
	Category.find({}, function(err, category) {
		console.log("machine Category = ", category);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		result.category = category;

		SubCategory.find({}, function(err, subCategory) {
		console.log("machine Sub Category = ", subCategory);
		if(err)
			{				
				console.log("error = ", err);
				deferred.reject(new Error(err));
			}
			result.subCategory = subCategory;

			Parts.find({}, function(err, parts) {
				console.log("Part = ", parts);
				if(err){
					console.log("error = ", err);
					deferred.reject(new Error(err));
				}
				result.parts = parts;
				if(result){
					deferred.resolve({
						'result' : result,
						'status' : "success"
					});
				}
			});
		});
		
	});
	return deferred.promise;
}


module.exports = {addNewSnag : addNewSnag,
			  	  listAllSnag : listAllSnag,
			  	  deleteSnag : deleteSnag,
			  	  updateSnag : updateSnag,
			  	  getSnag : getSnag,
			  	  listOfSnagBySnagType : listOfSnagBySnagType,
			  	  updateCurrentStatus : updateCurrentStatus,
			  	  getAllCategorySubCategoryParts : getAllCategorySubCategoryParts,
			  	  };