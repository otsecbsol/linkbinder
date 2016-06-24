/*
================================================================================
	Name			:	profiler
	In			:	[none]
	Out			:	[none]
	Note			:	処理時間を計測する
--------------------------------------------------------------------------------
	Version		:	Ver1.0.0		|	2009/02/07	|	新規作成
--------------------------------------------------------------------------------
	License		:	MIT license
	URL			:	www.kanasansoft.com
================================================================================
*/

var profiler=(
	function(){
		var timeRun={};
		var resultData={};
		var subResultData={};
		var startForMain=function(name){
			if((name in resultData)&&resultData.hasOwnProperty(name)){
			}else{
				timeRun[name]=[];
				resultData[name]={
					"count":0,
					"sum":0,
					"min":Number.POSITIVE_INFINITY,
					"max":Number.NEGATIVE_INFINITY
				};
				subResultData[name]={};
			}
			var body={
				"time":new Date().getTime(),
				"index":timeRun[name].length
			};
			timeRun[name].push(body);
			return getForChain(name,body);
		}
		var stopForMain=function(name,subname){
			if(timeRun[name].length==0){
				return false;
			}
			var timeCur=timeRun[name].pop();
			var passed=new Date().getTime()-timeCur.time;
			saveResult(name,subname,passed);
		}
		var stopForChain=function(name,body){
			return function(subname){
				var index=void(0);
				for(var i=Math.min(body.index,timeRun[name].length-1);i>=0;i--){
					if(body==timeRun[name][i]){
						index=i;
						break;
					}
				}
				if(index==void(0)){
					return false;
				}
				var timeCur=timeRun[name].slice(index,index+1)[0];
				var passed=new Date().getTime()-timeCur.time;
				saveResult(name,subname,passed);
				timeRun[name]=timeRun[name].slice(0,index).concat(timeRun[name].slice(index+1));
			}
		};
		var getForChain=function(name,body){
			return {
				"stop":stopForChain(name,body),
				"dummy":function(){return this;}
			}
		}
		var saveResult=function(name,subname,passed){
			if(subname){
				if((subname in subResultData[name])&&subResultData[name].hasOwnProperty(subname)){
				}else{
					subResultData[name][subname]={
						"count":0,
						"sum":0,
						"min":Number.POSITIVE_INFINITY,
						"max":Number.NEGATIVE_INFINITY
					};
				}
				subResultData[name][subname].count++;
				subResultData[name][subname].sum+=passed;
				subResultData[name][subname].min=Math.min(subResultData[name][subname].min,passed);
				subResultData[name][subname].max=Math.max(subResultData[name][subname].max,passed);
			}else{
				resultData[name].count++;
				resultData[name].sum+=passed;
				resultData[name].min=Math.min(resultData[name].min,passed);
				resultData[name].max=Math.max(resultData[name].max,passed);
			}
		}
		var result=function(){
			var results=[];
			results.push(
			    [
					"start name",
					"stop name",
					"count",
					"minimal time(ms)",
					"maximum time(ms)",
					"total time(ms)",
					"average time(ms)"
			    ].join("\t")
			);
			for(var name in resultData){
			    if(resultData.hasOwnProperty(name)){
					var running=[
						"["+name+"]",
						"(running)",
						timeRun[name].length,
						"-",
						"-",
						"-",
						"-"
					].join("\t");
					var buf={
						"count":0,
						"sum":0,
						"min":Number.POSITIVE_INFINITY,
						"max":Number.NEGATIVE_INFINITY
					};
					var subResults=[];
					if(resultData[name].count!=0){
						buf.count+=resultData[name].count;
						buf.sum+=resultData[name].sum;
						buf.min=Math.min(resultData[name].min,buf.min);
						buf.max=Math.max(resultData[name].max,buf.max);
						subResults.push(
							[
								"-",
								"(no name)",
								resultData[name].count,
								resultData[name].min,
								resultData[name].max,
								resultData[name].sum,
								resultData[name].sum/resultData[name].count
							].join("\t")
						);
					}
					for(var subname in subResultData[name]){
						if(subResultData[name].hasOwnProperty(subname)){
							buf.count+=subResultData[name][subname].count;
							buf.sum+=subResultData[name][subname].sum;
							buf.min=Math.min(subResultData[name][subname].min,buf.min);
							buf.max=Math.max(subResultData[name][subname].max,buf.max);
							subResults.push(
								[
									"-",
									"["+subname+"]",
									subResultData[name][subname].count,
									subResultData[name][subname].min,
									subResultData[name][subname].max,
									subResultData[name][subname].sum,
									subResultData[name][subname].sum/subResultData[name][subname].count
								].join("\t")
							);
						}
					}
					results.push(running);
					if(buf.count!=0){
						results.push(
							[
								"-",
								"(all)",
								buf.count,
								buf.min,
								buf.max,
								buf.sum,
								buf.sum/buf.count
							].join("\t")
						);
					}
					results=results.concat(subResults);
				}
			}
			return results.join("\n");
		}
		return {
			"start":startForMain,
			"stop":stopForMain,
			"result":result
		}
	}
)();
