// load the things we need
var mongoose = require('mongoose');

// define the schema for our user model
var snagSchema = mongoose.Schema({
      machineid : String,
      category : String,
      subcategory : String,
      partname : String,
      description : String,
      inspector1 : String,
      functionaloperator : String,
      inspector2 : String,
      inspector3 : String,
      currentstatus : String,
      created: {
        type: Date,
        default: Date.now
      },

});


// create the model for users and expose it to our app
module.exports = mongoose.model('SnagCollection', snagSchema);
