var app=angular.module('App',['ngRoute','ngSanitize', 'ui.select', 'ui.bootstrap']);

app.directive('btnSalvarCancelarExcluir', function () {
    return {
        template:   '<input class="btn btn-raf" type="submit" ng-click="save(row)" value="Salvar" />'+
        			'<a class="btn btn-default" ng-click="cancelar()" style="margin-left: 3px">Cancelar</a>'+
        			'<a class="btn btn-danger" ng-click="deletar(row)" ng-show="row.id > 0" style="margin-left: 3px" >Excluir</a>'

    };

});

app.directive('select2',['$routeParams', function ($routeParams) {

	return {
		scope: {model: '@model', dataText: '@dataText', url:'@url', placeholder:'@placeholder', onSelect:'@onselect', width:'@width'},
		restrict: 'E',
		template: '',
		compile: function(tElement, tAttrs,routeParams) {

			var uu = "'" + tAttrs.url + "listSelect2'";
			var str = tAttrs.model;
			var idd = str.substr(str.indexOf(".")+1);

			var html =
				' <ui-select id="'+idd+'" ng-model="' + tAttrs.model + '" on-select="'+tAttrs.onselect+'" theme="bootstrap" ng-disabled="disabled" reset-search-input="false" '+
				'	style="width:'+ tAttrs.width+'">  '+
				'	<ui-select-match placeholder="' + tAttrs.placeholder + '">{{$select.selected.'+ tAttrs.datatext+'}}</ui-select-match> '+
				'	<ui-select-choices repeat="item in list' + idd  + ' track by $index" ' +
				'refresh="refreshSelect2(' + uu + ', $select.search, ' + "'list" + idd  + "'"  + ')" refresh-delay="400"> ' +
				'		<div ng-bind-html="item.'+ tAttrs.datatext+' | highlight: $select.search"></div> '+
				'	</ui-select-choices> ' +
				' </ui-select> ';

			tElement.append(html);
		}

	}

}]);

app.factory('httpRequestInterceptor', function ($q, $location, $window) {
    return {
        'responseError': function(rejection) {

        	console.log(rejection);

        	// do something on error
            if(rejection.status === 404){
                $location.path('/404/');
            }
            else if(rejection.status === 403){
            	$location.path('/403/');
            }
            return $q.reject(rejection);
         },'response': function(response) {



         	return response;
         }
     };
});


app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.
      when('/produtos', {
        templateUrl: 'product_list.html',
        controller: 'ProductCtrl'
      }).
      when('/produtos/:id', {
        templateUrl: 'product_crud.html',
        controller: 'ProductCtrl'
      }).
      when('/clientes', {
        templateUrl: 'cliente_list.html',
        controller: 'ClienteCtrl'
      }).
      when('/clientes/:id', {
        templateUrl: 'cliente_crud.html',
        controller: 'ClienteCtrl'
      }).
      when('/pedidos', {
        templateUrl: 'pedido_list.html',
        controller: 'PedidoCtrl'
      }).
      when('/pedidos/:id', {
        templateUrl: 'pedido_crud.html',
        controller: 'PedidoCtrl'
      }).
      when('/inicio/', {
        templateUrl: 'inicio.html',
        //controller: 'ProductCtrl'
      }).
      otherwise({
        redirectTo: '/inicio'
      });

}]);


/*
app.config(function($httpProvider, $interpolateProvider, $routeProvider){

	  //$httpProvider.interceptors.push('httpRequestInterceptor');

	  $routeProvider.when('/', {redirectTo: 'inicio.html'});

      $routeProvider.when('/:recurso*',{
    	  templateUrl: function(urlattr){
    		  console.log(urlattr);
    		  return '/' + urlattr.recurso;
    	  },
    	  //controller : 'GenericCtrl'
      });


});

*/

app.filter('propsFilter', function() {
	console.log("FILTER");
	return function(items, props) {
	    var out = [];

	    if (angular.isArray(items)) {
	      items.forEach(function(item) {
	        var itemMatches = false;

	        var keys = Object.keys(props);
	        for (var i = 0; i < keys.length; i++) {
	          var prop = keys[i];
	          var text = props[prop].toLowerCase();
	          if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
	            itemMatches = true;
	            break;
	          }
	        }

	        if (itemMatches) {
	          out.push(item);
	        }
	      });
	    } else {
	      // Let the output be the input untouched
	      out = items;
	    }

	    return out;
	  }
});

app.controller('ProductCtrl', function($scope, $controller, $http, $location, $routeParams) {


  $scope.getList = function() {

    $http.get('/api/products/').success(function(data){
        $scope.rows = data;
      });
  }

  $scope.getById = function() {

    var id = $routeParams.id;

    if(id != 'novo'){

      $http.get('/api/products/'+id).success(function(data){
          $scope.row = data;
        });

    }


  }



  $scope.edit = function(codigo) {

        $location.path('/produtos/'+codigo);

  }

  $scope.save = function(obj) {


    var req = { method: 'POST', url: '/api/products/', data:obj}

    		$http(req).success(function(data, status, headers, config) {

            $location.path('/produtos');

    				$.notify({message: 'Registro salvo com sucesso.'},
                     {type: 'success'});

    		}).
    		error(function(data, status, headers, config) {

          $.notify({message: 'Erro ao salvar registro'},
                   {type: 'danger'});

    		});


  }





});

app.controller('ClienteCtrl', function($scope, $controller, $http, $location, $routeParams) {


  $scope.getList = function() {

    $http.get('/api/clientes/').success(function(data){
        $scope.rows = data;
      });
  }

  $scope.getById = function() {

    var id = $routeParams.id;

    if(id != 'novo'){

      $http.get('/api/clientes/'+id).success(function(data){
          $scope.row = data;
        });

    }


  }



  $scope.edit = function(codigo) {

        $location.path('/clientes/'+codigo);



  }

  $scope.save = function(obj) {


    var req = { method: 'POST', url: '/api/clientes/', data:obj}

    		$http(req).success(function(data, status, headers, config) {

            $location.path('/clientes');

    				$.notify({message: 'Registro salvo com sucesso.'},
                     {type: 'success'});

    		}).
    		error(function(data, status, headers, config) {

          $.notify({message: 'Erro ao salvar registro'},
                   {type: 'danger'});

    		});


  }





});


app.controller('PedidoCtrl', function($scope, $controller, $http, $location, $routeParams) {

  $scope.open = function($event) {
		$event.preventDefault();

		$scope.opened = true;
	};

	$scope.format = 'dd/MM/yyyy';

	$scope.dateOptions = {
	   startingDay : 1,
	   showWeeks : false
	};

  $scope.novoItem = function () {

		$scope.row.produtos.push({});


	};

  $scope.removeItem = function(index) {
		$scope.row.produtos.splice(index, 1);

	}

  $scope.total = function() {

		var total = 0;

		if ($scope.row != null) {

			angular.forEach($scope.row.produtos, function(item) {
				total += (item.quantidade * item.vlrUnit);

			})

			$scope.row.vlrTotal = total;
		}



		return total;
	}


  $scope.onSelectItem = function (item, model, index){

  		$scope.row.produtos[index].vlrUnit = item.valor;

      // se a quantidade for vazia atribui 1.
      if($scope.row.produtos[index].quantidade == undefined){
        $scope.row.produtos[index].quantidade = 1;
      }

  }

  $scope.getList = function() {

    $http.get('/api/pedidos/').success(function(data){
        $scope.rows = data;
      });
  }

  $scope.getById = function() {

    var id = $routeParams.id;

    if(id != 'novo'){

      $http.get('/api/pedidos/'+id).success(function(data){
          $scope.row = data;
        });

    }
    else {
      $scope.row = {};
      $scope.row.produtos = [];
			$scope.row.produtos.push({});

    }


  }

  $scope.refreshSelect2 = function(url,term, listt) {

		var params = {term: term};
		return $http.get(url,{params: params}).then(function(response) {
			$scope[listt] = response.data
		});

	};




  $scope.edit = function(codigo) {

        $location.path('/pedidos/'+codigo);



  }

  $scope.save = function(obj) {


    var req = { method: 'POST', url: '/api/pedidos/', data:obj}

    		$http(req).success(function(data, status, headers, config) {

            $location.path('/pedidos');

    				$.notify({message: 'Registro salvo com sucesso.'},
                     {type: 'success'});

    		}).
    		error(function(data, status, headers, config) {

          $.notify({message: 'Erro ao salvar registro'},
                   {type: 'danger'});

    		});


  }





});
