"use strict";

let module = angular.module('Customers', ['ngResource']);

module.factory('getCustomers', function ($resource) {
    return $resource('http://localhost:7081/api/accounts');
});

module.factory('createCustomer', function ($resource) {
    return $resource('http://localhost:7081/api/accounts');
});

module.controller('CustomerController', function (getCustomers, createCustomer) {
    this.customers - getCustomers.query();
    this.createAccount = function (customer) {
        createCustomer.save(null, customer, function() {
            this.customers = getCustomers.query();
        });
    };
});