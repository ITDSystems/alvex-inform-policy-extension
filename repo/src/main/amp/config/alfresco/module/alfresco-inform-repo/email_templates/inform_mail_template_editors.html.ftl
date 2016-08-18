Hello ${args.firstName}<#if args.lastName?exists> ${args.lastName}</#if>!
You are editor of document ${args.documentname} that was updated.
New version: ${args.versionlabel}
Editor: ${args.lasteditorname}
Update time: ${args.updatedate}

Regards