"use strict";

let module = angular.module('Customers', ['ngResource']);

module.factory('createCustomerJetty', function ($resource) {
    return $resource('http://localhost:9000/createaccount', null, {update: {method: 'POST'}});
});

module.controller('CustomerController', function (createCustomerJetty) {
    this.createAccount = function (customer) {
        createCustomerJetty.save({}, customer, function() {
            window.location.reload();
        });
    };
});