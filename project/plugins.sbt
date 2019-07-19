// Comment to get more information during initialization
logLevel := Level.Info

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-web"  % "1.0.2")

addSbtPlugin("com.jmparsons.sbt" % "sbt-dustjs-linkedin" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.0")