/**
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.sparkta.driver.actor

import scala.reflect.runtime.universe._

import akka.actor._
import com.gettyimages.spray.swagger.SwaggerHttpService
import com.wordnik.swagger.model.ApiInfo
import spray.routing._

import com.stratio.sparkta.driver.route.PolicyRoutes

/**
 * Created by ajnavarro on 29/10/14.
 */
class PolicyControllerActor( val supervisor2: ActorRef)  extends HttpServiceActor {

  override  implicit def actorRefFactory: ActorContext = context
  val polices=new PolicyRoutes {
     override val supervisor: ActorRef = supervisor2
     def actorRefFactory: ActorRefFactory = actorRefFactory
  }

  def receive: Receive = runRoute(polices.policyRoutes ~ swaggerService.routes~
    get {
      pathPrefix("swagger") { pathEndOrSingleSlash {
        getFromResource("swagger-ui/index.html")
      }
      } ~
        getFromResourceDirectory("swagger-ui")
    })

  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[PolicyRoutes])
    override def apiVersion = "1.0"
    override def baseUrl = "/" // let swagger-ui determine the host and port
    override def docsPath = "api-docs"
    override def actorRefFactory = context
    override def apiInfo = Some(new ApiInfo("SpaRkTA", "A real time aggregation engine full spark based.",
      "TOC" +
      " Url", "Sparkta@stratio.com", "Apache V2", "http://www.apache.org/licenses/LICENSE-2.0"))
  }
}
