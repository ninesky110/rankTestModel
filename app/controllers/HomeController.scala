package controllers

import java.util.concurrent.ConcurrentHashMap

import akka.actor.ActorSystem

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import akka.util.ByteString
import scala.concurrent.duration._

import scala.collection.JavaConverters._
import javax.inject._
import ml.dmlc.xgboost4j.scala.{Booster, XGBoost}
import play.api.mvc.Results.Ok
import play.api.mvc._

import scala.concurrent.duration.FiniteDuration
import scala.util.parsing.json.JSON


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem) extends AbstractController(cc) {
//  def index = Action {
//    Ok(views.html.index("Your new application is ready."))
//  }

  private val modelMap = new ConcurrentHashMap[String, Booster](10)
  private var count=0
  private val confPath = "/root/test_model/xg_test_model"
//private val confPath = "/Users/zhangxu/qt_project/xg_test_model"
  def modelUpdate(){
    val newModelMap = getModelMap(confPath)
    if (newModelMap.nonEmpty) {
      val newVersions = newModelMap.keySet
      //zhangxu add code
      val keys = modelMap.keys().asScala.toList
      keys.foreach(key => {
        modelMap.get(key).finalize()
        modelMap.remove(key)
      })
      modelMap.clear()
      newModelMap.foreach { case (version, model) => modelMap.put(version, model) }
      count=count+1
      println(s"reloaded xgboost models: ${count}")
    }
  }

  def reload: Action[AnyContent] = Action { implicit request =>
    modelUpdate()
    Ok(s"Models reloaded ${count}")
  }

  private def getModelMap(confPath: String): Map[String, Booster] = {
    //to Simulate importing multiple models
    for(index<-0 to 5){
      XGBoost.loadModel(confPath)
    }
    val modelMap = {
      val model = XGBoost.loadModel(confPath)
      Map("v1" -> model)
    }
    modelMap
  }


}
