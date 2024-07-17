var express = require('express');

var uController = require('../controller/userController');
var pController = require('../controller/productController');

var router = express.Router();

router.get('/', function(req, res, next) {
  res.render('index', { title: 'Projeto PSI' });
});

router.get("/getUsers", uController.getUsers)
router.get("/getUser/:username", uController.getUser)
router.post("/postUser", uController.postUser)
router.put("/putUser/:id", uController.putUser)

router.get("/getProducts", pController.getProducts)
router.get("/getProduct/:id", pController.getProduct)

module.exports = router;