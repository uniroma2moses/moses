function y=subsetc(x,k,sol,pre);

% FUNCTION y=subsetc(x,k,sol,pre)
%
% function subsets is an auxilliary recursive function used 
% by the function powerset to generate the powerset of a given set


y=[];

if isempty(x) return; end


if (k>1)

	for i=1:length(x)
        prex=[pre x(i)];
        appy=subsetc(x(i+1:end),k-1,sol,prex);
        y=[y appy];
	end

else

    for i=1:length(x)
        y{end+1}=[pre x(i)];    
    end

end