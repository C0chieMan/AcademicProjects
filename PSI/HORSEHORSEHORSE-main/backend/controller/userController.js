const { nextTick } = require('async')
var User = require('../models/usermodel')

exports.getUsers = function(req, res, next) {
    User.find({}).exec(function(err, list_users) {
            if (err) {
                return next(err) 
            }
            res.json(list_users)
        })
}

exports.getUser = function(req, res, next) {
    User.find({ username: req.params.username }).exec(function(err, user) {
        if (err) {
            return next(err)
        }
        res.json(user)
    })
}

exports.postUser = function(req, res, next) {
    var user = new User({
        username: req.body.username,
        password: req.body.password,
        imageProfile: req.body.imageProfile,
        wishlist: req.body.wishlist,
        library: req.body.library
    });
    User.findOne({'username': req.body.username}).exec(function(err, foundName) {
        if (err) { 
            return next(err)
        }
        user.save(function(err) {
            if (err) {
                return next(err)
            }
            res.redirect(user.url)
        })
    })
}

exports.putUser = function(req, res, next) {
    User.findByIdAndUpdate(req.params.id, {
        username: req.body.username,
        password: req.body.password,
        imageProfile: req.body.imageProfile,
        wishlist: req.body.wishlist,
        library: req.body.library
    }).exec(function(err, user) {
        if (err) {
            return next(err)
        }
        res.json({mensagem : "Successfully done"})
    })
}

exports.deleteUser = function(req, res, next) {
    User.findByIdAndDelete(req.params.id).exec(function(err) {
        if (err) { 
            return next(err)
        }
        res.json({mensagem: "User removed"})
    })
}