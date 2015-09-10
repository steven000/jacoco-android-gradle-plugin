package com.dicedmelon.gradle.jacoco.android

import com.android.build.gradle.internal.coverage.JacocoPlugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

class JacocoAndroidPluginSpec extends Specification {

  Project project

  def setup() {
    project = ProjectBuilder.builder().build()
  }

  def "should throw if android plugin not applied"() {
    when:
    project.apply plugin: 'jacoco-android'

    then:
    thrown PluginApplicationException
  }

  @Unroll
  def "should apply jacoco plugin by default with #androidPlugin"() {
    expect:
    project.apply plugin: androidPlugin
    project.apply plugin: 'jacoco-android'
    project.plugins.hasPlugin(JacocoPlugin)

    where:
    androidPlugin << ['com.android.library', 'com.android.application']
  }

  def "should add JacocoReport tasks for each variant"() {
    when:
    project = AndroidProjectFactory.library()
    project.apply plugin: JacocoAndroidPlugin
    project.evaluate()

    then:
    project.tasks.findByName("jacocoTestPaidDebugUnitTestReport")
    project.tasks.findByName("jacocoTestFreeDebugUnitTestReport")
    project.tasks.findByName("jacocoTestPaidReleaseUnitTestReport")
    project.tasks.findByName("jacocoTestFreeReleaseUnitTestReport")
    project.tasks.findByName("jacocoTestReport")
  }

  @Unroll
  def "should work with Android Gradle plugin #androidPluginVersion"() {
    when:
    project = AndroidProjectFactory.create()
    project.buildscript {
      repositories {
        jcenter()
      }
      dependencies {
        classpath "com.android.tools.build:gradle:$androidPluginVersion"
      }
    }
    AndroidProjectFactory.configureAsLibrary(project)
    project.apply plugin: JacocoAndroidPlugin
    project.evaluate()

    then:
    project.tasks.findByName("jacocoTestPaidDebugUnitTestReport")
    project.tasks.findByName("jacocoTestFreeDebugUnitTestReport")
    project.tasks.findByName("jacocoTestPaidReleaseUnitTestReport")
    project.tasks.findByName("jacocoTestFreeReleaseUnitTestReport")
    project.tasks.findByName("jacocoTestReport")

    where:
    // '1.3.1', '1.2.3', '1.1.3',
    androidPluginVersion << ['0.14.4', '0.1']
  }
}
