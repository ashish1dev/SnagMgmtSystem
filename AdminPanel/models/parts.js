// load the things we need
var mongoose = require('mongoose');

var partsSchema = mongoose.Schema( {
	category : String,
	subcategory : String,
	partname : String,
	created: {
        type: Date,
        default: Date.now
     },
});


// create the model for users and expose it to our app
module.exports = mongoose.model('PartsCollection', partsSchema);