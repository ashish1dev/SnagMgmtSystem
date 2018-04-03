// load the things we need
var mongoose = require('mongoose');
var autoIncrement = require('mongoose-auto-increment');


// define the schema for our snag model
var snagSchema = mongoose.Schema({
      snagID : String,
      machineID : String,
      category : String,
      subCategory : String,
      partName : String,
      description : String,
      inspector1UserName : String,
      functionalOperatorUserName : String,
      inspector2UserName : String,
      inspector3UserName : String,
      currentStatusOfSnag : String,
      created: {
        type: Date,
        default: Date.now
      },
});

autoIncrement.initialize(mongoose.connection);
snagSchema.plugin(autoIncrement.plugin, {
  model: 'snagSchema',
  field: 'snagID',
  startAt: 1,
  incrementBy: 1
});


// create the model for users and expose it to our app
module.exports = mongoose.model('SnagCollection', snagSchema);
