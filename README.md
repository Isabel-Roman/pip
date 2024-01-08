Instrucciones para realizar un PIP
===================================

Los pasos están indicados en https://is.docs.wso2.com/en/latest/develop/writing-a-custom-policy-info-point/ 

* Editar la clase que extienda AbstractPIPAttributeFinder (en este caso EnvironmentAttributeFinder)
* Crear el .jar ejecutando mvn clean install
* Copiar el .jar al IS, en el directorio /repository/components/lib
* Añadir al fichero deploiment.toml el pip creado
  
`[identity.entitlement.policy_point.pip]
attribute_designators = [
"us.dit.pip.EnvironmentAttributeFinder"
]`
