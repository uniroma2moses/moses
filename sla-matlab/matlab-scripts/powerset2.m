function y=powerset2(x,k);

% FUNCTION powerset y=powerset2(x,k)
%
% input: x array, k integer
% output: y at most k-elements powerset of array x
%
% the function powerset returns the powerset of array x. Only
% subsets of length at most k are included 

y=[];
for i=1:min(k,length(x))
    app=subsetc(x,i,[],[]);       
    y=[y app];
end

