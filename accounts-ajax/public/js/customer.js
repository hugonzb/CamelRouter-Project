"use strict";

let module = angular.module('Customers', ['ngResource']);

module.factory('getCustomers', function ($resource) {
    return $resource('http://localhost:7081/api/accounts');
});

module.factory('createCustomer', function ($resource) {
    return $resource('http://localhost:7081/api/accounts', null, {update: {method: 'POST'}});
});

module.controller('CustomerController', function (getCustomers, createCustomer) {
    let ctrl = this;
    ctrl.customers - getCustomers.query();
    this.createAccount = function (customer) {
        createCustomer.save({}, customer, function() {
            ctrl.customers = getCustomers.query();
        });
    };
});