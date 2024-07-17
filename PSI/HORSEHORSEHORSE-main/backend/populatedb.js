var async = require('async')

var Product = require('./models/productmodel')

var mongoose = require('mongoose');
var mongoDB = 'mongodb://psi024:psi024@127.0.0.1:27017/psi024?retryWrites=true&authSource=psi024';
mongoose.connect(mongoDB, {useNewUrlParser: true, useUnifiedTopology: true});
mongoose.Promise = global.Promise;
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));

var products = []

function productCreate(name, tipo, description, price, imageUrl, rate, plataform, idioma, youtubeURL, otherImages, cb) {
    productDetail = {
        tipo: tipo,
        name: name,
        description: description,
        imageUrl: imageUrl,
        price: price,
        rate: rate,
        plataform: plataform,
        idioma: idioma,
        youtubeURL: youtubeURL,
        otherImages: otherImages
    };

    var product = new Product(productDetail);

    product.save(function(err) {
        if (err) {
            cb(err, null)
            return
        }
        console.log("New Product: " + product);
        products.push(product);
        cb(null, product)
    });
}

function createProducts(cb) {
    async.series([
        function(callback) {
            productCreate(
                'FIFA 23',
                'Jogo',
                'O FIFA 23 traz o Desporto Rei para o campo, com a Tecnologia HyperMotion2.',
                30,
                '../../assets/images/fifa23.webp',
                '4,4 stars',
                'PC',
                'PT/EN',
                'https://www.youtube.com/watch?v=qV78jqlKdko',
                ['assets/images/FIFA1.jpg', 'assets/images/FIFA2.jpg'],
                callback);
        },
        function(callback) {
            productCreate(
                'Super Mario Odyssey',
                'Jogo',
                'Super Mario Odyssey é um jogo de plataforma desenvolvido pela Nintendo .',
                30,
                '../../assets/images/odyssey.webp',
                '4,2 stars',
                'PS4',
                'PT/EN',
                'https://www.youtube.com/watch?v=G-Wn48geCJ8',
                ['assets/images/SM1.jpg', 'assets/images/SM2.jpg'],
                callback);
        },
        function(callback) {
            productCreate(
                'Among Us',
                'Jogo',
                'Among Us é um jogo eletrônico online, dos gêneros jogo em grupo e sobrevivência.',
                30,
                '../../assets/images/amongus.webp',
                '4 stars',
                'PS4',
                'PT/EN',
                'https://www.youtube.com/watch?v=keyRM3h_7tk',
                ['assets/images/SUS1.jpg', 'assets/images/SUS2.jpg'],
                callback);
        },
        function(callback) {
            productCreate(
                'FIFA POINTS',
                'DLC',
                'FIFA Ultimate Team Points or FUT points is a virtual currency used for in-game purchases to boost your FIFA Ultimate Team composition.',
                30,
                '../../assets/images/fifa-23-points.jpg',
                '4 stars',
                'PC',
                'PT/EN',
                'https://www.youtube.com/watch?v=rNfRlMswhE4',
                ['assets/images/points1.jpg', 'assets/images/points2.jpg'],
                callback);
        },
        function(callback) {
            productCreate(
                'PUBG Mobile - Street Cred Set DLC Digital CD Key',
                'DLC',
                'Capacete salva-vidas Se você vai ganhar, você também pode ganhar com talento em seu Street Cred Set!',
                30,
                '../../assets/images/pubg.jpg',
                '4,5 stars',
                'PC',
                'PT/EN',
                'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
                ['assets/images/pubg1.jpg', 'assets/images/PUBG2.jpg'],
                callback);
        },
    ], cb)
}

async.series([
    createProducts
],

function(err, results) {
    if (err) {
        console.log('FINAL ERR: ' + err)
    } else {
        console.log('Well done')
    }
    mongoose.disconnect();
});
