var User = require('../models/user');
var Parts = require('../models/parts');
var Q = require("q");
var Category = require('../models/machineCategory');
var SubCategory = require('../models/machineSubCategory');


var addNewParts = function(partName) {
		var deferred = Q.defer();
		var newParts = new Parts();
			newParts.partName = partName;
			newParts.save(function(err) {
			if(err) {
				console.log("error is here!");
				deferred.reject(new Error(err));
			}
			else{
				console.log("success in dumping newParts to db");
				deferred.resolve({
				'status' : 'success'
				});
			}	
		});

	    return deferred.promise;
};

var listAllParts = function (){
	var  deferred = Q.defer();
	Parts.find({}, function(err, parts) {
		if(err) {
			console.log("error");

			deferred.reject(new Error(err));
		}

		if(parts && parts != "undefined") {
			console.log("Got the Parts");
			deferred.resolve({
				'parts' : parts,
				status : 'success',
			});
		}
		else 
		{
			console.log("Parts not found");
			deferred.resolve({
				'parts' : null,
				status : 'partsNotFound',
			});
		}
	});
	return deferred.promise;
}

var deleteParts = function(id){
	var deferred = Q.defer();
	Parts.remove({'_id' : id}, function(err, parts) {
		console.log("parts = ", parts);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(parts && parts.result.n >= 1) {
			console.log("Deleted Parts = ", parts.result);
			deferred.resolve({
				'status': 'success',
			});
		}
		else if(parts && parts.result.n == 0)
		{
			console.log("Parts not found");
			deferred.resolve({
				status : 'noPartsFound',
			});
		}
	});
	return deferred.promise;
}

var getAllCategory = function(id) {
	var deferred = Q.defer();
	Category.find({}, function(err, category) {
		console.log("machine Category = ", category);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(category && category != "undefined") {
			deferred.resolve({
				'category' : category,
				'status': 'success',
			});
		}
		else{
			console.log("category not found");
			deferred.resolve({
				status : 'noCategoryFound',
			});
		}
	});
	return deferred.promise;
}

var getAllSubCategory = function(id) {
	var deferred = Q.defer();
	SubCategory.find({}, function(err, subCategory) {
		console.log("machine Sub Category = ", subCategory);
		if(err){
			console.log("error = ", err);
			deferred.reject(new Error(err));
		}
		if(subCategory && subCategory != "undefined") {
			deferred.resolve({
				'subCategory' : subCategory,
				'status': 'success',
			});
		}
		else{
			console.log("subCategory not found");
			deferred.resolve({
				status : 'noSubCategoryFound',
			});
		}
	});
	return deferred.promise;
}

module.exports = {addNewParts : addNewParts,
			  	  listAllParts : listAllParts,
			  	  deleteParts : deleteParts,
			  	  getAllCategory : getAllCategory,
			  	  getAllSubCategory : getAllSubCategory
			  	};
