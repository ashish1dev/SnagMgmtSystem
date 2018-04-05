// load the things we need
var mongoose = require('mongoose');

var partsSchema = mongoose.Schema( {
	partName : String
});


// create the model for users and expose it to our app
module.exports = mongoose.model('PartsCollection', partsSchema);
