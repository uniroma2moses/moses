function calcola_l(nodo,gamma,gamma_ori)

% FUNCTION calcola_l
%
% this recursive function computes for each servivce class the request rate arriving at each workflow node.
% the request rate is returned in the field lambda of each node.


global node

M=length(node(nodo).p);

for m=1:M
    
    lambda=[];
    
    pnodi=node(nodo).p{m};
    n=length(pnodi);
	
    gamma_v=zeros(n,1);
	
    inds=find(pnodi==node(nodo).pathsr(m));
    
    for k=1:length(gamma)
      gamma_v(inds)=gamma(k);
      lambda(:,k)=(eye(n)-abs(node(nodo).classe(k).proute{m})')\gamma_v;
    end   
    
    for i=1:n
        l=pnodi(i);
        if(node(l).tipo==0) % it is a workflow node
            node(l).lambda=lambda(i,:);
            node(l).visite=node(l).lambda./gamma_ori;
            % keyboard;
        elseif(node(l).tipo==10) % it is a fork/join node
            node(l).lambda=lambda(i,:);
            node(l).visite=node(l).lambda./gamma_ori;
            calcola_l(l,lambda(i,:),gamma_ori);
        end
    end
    
end