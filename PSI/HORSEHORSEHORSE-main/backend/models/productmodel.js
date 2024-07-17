var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var ProductModelSchema = new Schema({
    tipo: String,
    name: String,
    description: String,
    imageUrl: String,
    price: Number,
    rate: String,
    plataform: String,
    idioma: String,
    youtubeURL: String,
    otherImages: [String]
});

ProductModelSchema
    .virtual('url')
    .get(function() {
        return '/product/' + this._id;
    });

module.exports = mongoose.model('Product', ProductModelSchema);