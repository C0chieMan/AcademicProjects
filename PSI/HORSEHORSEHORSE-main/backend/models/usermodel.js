var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var UserModelSchema = new Schema({
    username: String,
    password: String,
    imageProfile: String,
    wishlist: [String],
    library: [{ productID: String, quantity: Number, purchasedAt: Date }]
});

UserModelSchema
    .virtual('url')
    .get(function() {
        return '/user/' + this._id;
    });

module.exports = mongoose.model('User', UserModelSchema);  
