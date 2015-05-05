// Declare app level module which depends on views, and components
angular.module('myApp', [
  'myApp.controllers',
  'ngRoute'
  ])
 .config(function($routeProvider) {
       
       $routeProvider.when('/', {
        templateUrl: 'index.html',
        controller: 'homeCtrl'
       })
       // use the HTML5 History API
       // $locationProvider.html5Mode(true);
     })