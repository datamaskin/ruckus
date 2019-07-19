#!/bin/bash
PS3="Choose(1-3)"
echo "Choose from the list below"
select name in "requirejs" "jshint" "exit"
do
        break
done

cd ../public

if [[ $name == "requirejs" ]]
then
	# note that the assets dir with symlinks to js and css exists solely to straighten out the paths which are caused by the play frameworks inclusion of assets directory
	node r.js -o app.build.js
fi

if [[ $name == "jshint" ]]
then
	jshint js/base.js
	jshint js/main.js
	jshint js/controller.js

	jshint js/pagecontrols/base.js
	jshint js/pagecontrols/error.js
	jshint js/pagecontrols/generic.js
	jshint js/pagecontrols/info.js
	jshint js/pagecontrols/main.js
	jshint js/pagecontrols/sectiondata.js
	jshint js/pagecontrols/settings.js
	jshint js/pagecontrols/transactionhistory.js
	jshint js/pagecontrols/dashboard.js
	jshint js/pagecontrols/faq.js
	jshint js/pagecontrols/header.js
	jshint js/pagecontrols/lobby.js
	jshint js/pagecontrols/privacy.js
	jshint js/pagecontrols/sectionone.js
	jshint js/pagecontrols/support.js
	jshint js/pagecontrols/withdrawl.js
	jshint js/pagecontrols/deposit.js
	jshint js/pagecontrols/footer.js
	jshint js/pagecontrols/howitworks.js
	jshint js/pagecontrols/loyaltybonus.js
	jshint js/pagecontrols/referafriend.js
	jshint js/pagecontrols/sectiontwo.js
	jshint js/pagecontrols/termsandconditions.js
	jshint js/pagecontrols/contestentry.js

	jshint js/subpagecontrols/avsbfilter.js
	jshint js/subpagecontrols/base.js
	jshint js/subpagecontrols/contestfilter.js
	jshint js/subpagecontrols/generic.js
	jshint js/subpagecontrols/orange.js
	jshint js/subpagecontrols/yellow.js
	jshint js/subpagecontrols/avsbresults.js
	jshint js/subpagecontrols/blue.js
	jshint js/subpagecontrols/contestresults.js
	jshint js/subpagecontrols/green.js
	jshint js/subpagecontrols/red.js

	jshint js/models/base.js
	jshint js/models/basews.js
	jshint js/models/contest.js
	jshint js/models/contestfilter.js
	jshint js/models/generic.js
fi
