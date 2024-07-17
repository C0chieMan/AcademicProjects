const { nextTick } = require('async')
var Product = require('../models/productmodel')

exports.getProducts = function(req, res, next) {
    Product.find({}).exec(function(err, list_products) {
            if (err) {
                return next(err) 
            }
            res.json(list_products)
        })
}

exports.getProduct = function(req, res, next) {
    Product.findById(req.params.id).exec(function(err, product) {
        if (err) {
            return next(err)
        }
        res.json(product)
    })
}

exports.postProduct = function(req, res, next) {
    var product = new Product({
        tipo: req.body.tipo,
        name: req.body.name,
        description: req.body.description,
        imageUrl: req.body.imageUrl,
        price: req.body.price,
        rate: req.body.rate,
        plataform: req.body.plataform,
        idioma: req.body.idioma,
        youtubeURL: req.body.youtubeURL,
        otherImages: req.body.otherImages
    });
    Product.findOne({'name': req.body.name}).exec(function(err, foundName) {
        if (err) { 
            return next(err)
        }
        product.save(function(err) {
            if (err) {
                return next(err)
            }
            res.redirect(product.url)
        })
    })
}

exports.putProduct = function(req, res, next) {
    Product.findByIdAndUpdate(req.params.id, {
        tipo: req.body.tipo,
        name: req.body.name,
        description: req.body.description,
        imageUrl: req.body.imageURL,
        price: req.body.price,
        rate: req.body.rate,
        plataform: req.body.plataform,
        idioma: req.body.idioma,
        youtubeURL: req.body.youtubeURL,
        otherImages: req.body.otherImages
    }).exec(function(err, product) {
        if (err) { 
            return next(err)
        }
        res.json({mensagem : "Successfully done"})
    })
}

exports.deleteProduct = function(req, res, next) {
    Product.findByIdAndDelete(req.params.id).exec(function(err) {
        if (err) { 
            return next(err)
        }
        res.json({mensagem: "Product removed"})
    })
}